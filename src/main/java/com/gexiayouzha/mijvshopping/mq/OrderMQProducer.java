package com.gexiayouzha.mijvshopping.mq;

import com.gexiayouzha.mijvshopping.config.RabbitMQConfig;
import com.gexiayouzha.mijvshopping.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /** 下单成功：一条立即通知 + 一条30分钟延迟消息 */
    public void sendOrderCreated(Order order) {
        OrderMessage msg = build(order);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RK_CREATE(), msg);//发送一条消息的意思. 参数: 交换机的名字(只有一个交换机), 路由键(而config里有配置路由键(代表消息)和哪个队列绑定), 消息. 路由键:"order.create"
        //路由键代表了哪个消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.RK_DELAY, msg);
        log.info("【MQ】下单消息已发送 orderId={}, orderNumber={}", order.getOrderId(), order.getOrderNumber());
    }

    /** 支付成功通知 */
    public void sendOrderPaid(Order order) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.RK_PAID, build(order));
        log.info("【MQ】支付成功消息已发送 orderId={}", order.getOrderId());
    }

    private OrderMessage build(Order order) {//把order里的信息提取出来, 变成一条消息
        return new OrderMessage(
                UUID.randomUUID().toString(),
                order.getOrderId(),
                order.getOrderNumber(),
                order.getUserId()
        );
    }

    private String RK_CREATE() {
        return RabbitMQConfig.RK_CREATE;
    }
}