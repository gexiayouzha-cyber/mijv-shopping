package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.RefundRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.RefundReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Order;
import com.gexiayouzha.mijvshopping.entity.OrderItem;
import com.gexiayouzha.mijvshopping.entity.Refund;
import com.gexiayouzha.mijvshopping.mapper.RefundMapper;
import com.gexiayouzha.mijvshopping.service.IOrderItemService;
import com.gexiayouzha.mijvshopping.service.IOrderService;
import com.gexiayouzha.mijvshopping.service.IRefundService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements IRefundService {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderItemService orderItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result applyRefund(RefundRequestDTO refundRequestDTO) {
        Long orderId = refundRequestDTO.getOrderId();
        Long orderItemId = refundRequestDTO.getOrderItemId();
        String refundType = refundRequestDTO.getRefundType();
        String refundReason = refundRequestDTO.getRefundReason();
        BigDecimal refundAmount = refundRequestDTO.getRefundAmount();

        Long userId = UserHolder.getUser().getUserId();

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }

        if (!userId.equals(order.getUserId())) {
            return Result.fail(403, "无权操作此订单");
        }

        if (!"W".equals(order.getOrderStatus()) && !"S".equals(order.getOrderStatus())) {
            return Result.fail(400, "订单状态不允许退款");
        }

        if (!isValidRefundType(refundType)) {
            return Result.fail(400, "退款类型不正确");
        }

        BigDecimal maxRefundAmount;
        if (orderItemId != null) {
            OrderItem orderItem = orderItemService.getById(orderItemId);
            if (orderItem == null || !orderId.equals(orderItem.getOrderId())) {
                return Result.fail(404, "订单商品不存在");
            }
            maxRefundAmount = orderItem.getTotalAmount().subtract(orderItem.getDiscountAmount());
        } else {
            maxRefundAmount = order.getActualPaidAmount();
        }

        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.fail(400, "退款金额必须大于0");
        }

        if (refundAmount.compareTo(maxRefundAmount) > 0) {
            return Result.fail(400, "退款金额超过可退金额");
        }

        Refund refund = new Refund();
        refund.setRefundNumber(generateRefundNumber());
        refund.setOrderId(orderId);
        refund.setOrderItemId(orderItemId);
        refund.setUserId(userId);
        refund.setRefundType(refundType);
        refund.setRefundReason(refundReason);
        refund.setRefundAmount(refundAmount);
        refund.setRefundStatus("P");
        refund.setCreateTime(LocalDateTime.now());

        save(refund);

        RefundReturnDTO refundReturnDTO = new RefundReturnDTO();
        refundReturnDTO.setRefundId(refund.getRefundId());
        refundReturnDTO.setRefundNumber(refund.getRefundNumber());
        refundReturnDTO.setRefundStatus(refund.getRefundStatus());

        return Result.success(200, "申请成功", refundReturnDTO);
    }

    private boolean isValidRefundType(String refundType) {
        return "R".equals(refundType) || "E".equals(refundType);
    }

    private String generateRefundNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
        int random = new Random().nextInt(900000) + 100000;
        return "R" + dateTime + random;
    }
}