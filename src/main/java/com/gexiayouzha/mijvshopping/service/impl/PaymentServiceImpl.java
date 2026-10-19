package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.PaymentCallbackDTO;
import com.gexiayouzha.mijvshopping.DTO.PaymentRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.PaymentReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Order;
import com.gexiayouzha.mijvshopping.entity.Payment;
import com.gexiayouzha.mijvshopping.mapper.PaymentMapper;
import com.gexiayouzha.mijvshopping.mq.OrderMQProducer;
import com.gexiayouzha.mijvshopping.service.IOrderService;
import com.gexiayouzha.mijvshopping.service.IPaymentService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements IPaymentService {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private OrderMQProducer orderMQProducer;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createPayment(PaymentRequestDTO paymentRequestDTO) {
        Long orderId = paymentRequestDTO.getOrderId();
        String paymentMethod = paymentRequestDTO.getPaymentMethod();

        Long userId = UserHolder.getUser().getUserId();

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }

        if (!userId.equals(order.getUserId())) {
            return Result.fail(403, "无权操作此订单");
        }

        if (!"P".equals(order.getOrderStatus())) {
            return Result.fail(400, "订单状态不允许支付");
        }

        if (order.getPaymentDeadline() != null && LocalDateTime.now().isAfter(order.getPaymentDeadline())) {
            return Result.fail(400, "支付已超时");
        }

        if (!isValidPaymentMethod(paymentMethod)) {
            return Result.fail(400, "支付方式不正确");
        }

        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentAmount(order.getActualPaidAmount());
        payment.setPaymentStatus("P");

        save(payment);

        PaymentReturnDTO paymentReturnDTO = new PaymentReturnDTO();
        paymentReturnDTO.setPaymentId(payment.getPaymentId());
        paymentReturnDTO.setPaymentNumber(payment.getPaymentNumber());
        paymentReturnDTO.setPaymentUrl(generatePaymentUrl(orderId));
        paymentReturnDTO.setQrCode(generateQrCode(orderId));

        return Result.success(200, "请求成功", paymentReturnDTO);
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return "A".equals(paymentMethod) || "W".equals(paymentMethod)
                || "B".equals(paymentMethod) || "C".equals(paymentMethod)
                || "P".equals(paymentMethod);
    }

    // TODO这个应该是支付宝生成发给购物平台的吧, 应该不用写这个generate方法吧
    private String generatePaymentNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
        int random = new Random().nextInt(900000) + 100000;
        return "P" + dateTime + random;
    }

    private String generatePaymentUrl(Long orderId) {
        return "https://example.com/pay?order=" + orderId;
    }

    private String generateQrCode(Long orderId) {
        return "data:image/png;base64,QR_CODE_BASE64_DATA_" + orderId;
    }
    /*    TODO - paymentUrl 目前是模拟的支付链接，实际项目中需要替换为真实的第三方支付平台链接（如支付宝、微信支付）
            - qrCode 目前是占位符，实际项目中需要引入二维码生成库（如 ZXing）生成真实的 Base64 编码二维码图片*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result paymentCallback(PaymentCallbackDTO callbackDTO) {
        String paymentNumber = callbackDTO.getPaymentNumber();
        String transactionId = callbackDTO.getTransactionId();
        String paymentStatus = callbackDTO.getPaymentStatus();

        Payment payment = lambdaQuery()
                .eq(Payment::getPaymentNumber, paymentNumber)
                .one();

        if (payment == null) {
            return Result.fail(404, "支付记录不存在");
        }

        if ("S".equals(payment.getPaymentStatus())) {
            return Result.success(200, "回调成功", null);
        }

        payment.setTransactionId(transactionId);
        payment.setPaymentStatus(paymentStatus);
        payment.setNotifyTime(LocalDateTime.now());

        Order order = null;
        if ("S".equals(paymentStatus)) {
            payment.setPaymentTime(LocalDateTime.now());

            order = orderService.getById(payment.getOrderId());
            if (order != null) {
                order.setOrderStatus("W");
                order.setPaymentTime(LocalDateTime.now());
                orderService.updateById(order);
            }
        }
        updateById(payment);
        if(order!=null){
            final Order paidOrder = order; // 供匿名内部类使用
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    orderMQProducer.sendOrderPaid(paidOrder);
                }
            });
        }

        return Result.success(200, "回调成功", null);
    }

}