package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.OrderCancelDTO;
import com.gexiayouzha.mijvshopping.DTO.OrderCreateDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @PostMapping
    public Result addOrder(@RequestBody OrderCreateDTO orderCreateDTO,
@RequestHeader(value = "Idempotency", required = false) String idempotencyKey
    ) {
        return orderService.addOrder(
                idempotencyKey,
                orderCreateDTO.getAddressId(),
                orderCreateDTO.getCartIds(),
                orderCreateDTO.getItems(),
                orderCreateDTO.getCouponId(),
                orderCreateDTO.getUseBonusPoints(),
                orderCreateDTO.getOrderRemarks());
    }

    @GetMapping
    public Result listOrder(@RequestParam(required = false) String status,
                            @RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer pageSize) {
        return orderService.listOrder(status, page, pageSize);
    }

    @GetMapping("/{orderId}")
    public Result getOrderDetails(@PathVariable Long orderId){
        return orderService.getOrderDetail(orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public Result cancelOrder(@PathVariable Long orderId,
                              @RequestBody OrderCancelDTO orderCancelDTO) {
        return orderService.cancelOrder(orderId, orderCancelDTO.getCancelReason());
    }

    @PutMapping("/{orderId}/confirm")
    public Result confrim(@PathVariable Long orderId) {
        return orderService.confirm(orderId);
    }

    @GetMapping("/{orderId}/logistics")
    public Result getLogistics(@PathVariable Long orderId) {
        return orderService.getLogistics(orderId);
    }

}
