package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.OrderItemDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Order;
import com.gexiayouzha.mijvshopping.entity.OrderItem;

import java.util.List;

public interface IOrderService extends IService<Order> {
    Result addOrder( String idempotencyKey, Long addressId, List<Long> cartIds, List<OrderItemDTO> items, Long couponId, Integer useBonusPoints, String orderRemarks);

    Result listOrder(String status, Integer page, Integer pageSize);

    Result getOrderDetail(Long orderId);

    Result cancelOrder(Long orderId, String cancelReason);

    Result confirm(Long orderId);

    Result getLogistics(Long orderId);
    /** MQ延迟关单：超时未支付则系统自动取消 */
    void autoCancelExpired(Long orderId);
}
