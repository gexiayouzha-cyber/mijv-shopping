package com.gexiayouzha.mijvshopping.mq;

import com.gexiayouzha.mijvshopping.config.RabbitMQConfig;
import com.gexiayouzha.mijvshopping.service.IOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderMQConsumer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IOrderService orderService;

    /** 下单通知 */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CREATE)
    public void handleCreate(OrderMessage msg, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();//消息投递的编号
        if (isDuplicate("create", msg.getMsgId())) {//判断该消息有没有处理过
            channel.basicAck(tag, false);//告诉rabbitMQ这条信息不用再处理了
            return;
        }
        try {
            log.info("【下单通知】用户{}下单成功，订单号={}, orderId={}",
                    msg.getUserId(), msg.getOrderNumber(), msg.getOrderId());
            // TODO 真实场景：发短信 / 站内信 / 邮件 / App推送
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("【下单通知】处理失败 orderId={}", msg.getOrderId(), e);
            channel.basicNack(tag, false, true); // 重新入队
        }
    }

    /** 支付成功通知 */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAID)
    public void handlePaid(OrderMessage msg, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        if (isDuplicate("paid", msg.getMsgId())) {
            channel.basicAck(tag, false);
            return;
        }
        try {
            log.info("【支付成功通知】订单号={} 已支付，等待商家发货", msg.getOrderNumber());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("【支付成功通知】处理失败 orderId={}", msg.getOrderId(), e);
            channel.basicNack(tag, false, true);
        }
    }

    /** 30分钟延迟关单 */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CLOSE)
    public void handleClose(OrderMessage msg, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        if (isDuplicate("close", msg.getMsgId())) {
            channel.basicAck(tag, false);
            return;
        }
        try {
            // 业务状态机幂等在 service 内部：只有 P 才取消，已支付直接跳过
            orderService.autoCancelExpired(msg.getOrderId());
            log.info("【延迟关单】处理完成 orderId={}", msg.getOrderId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("【延迟关单】处理失败 orderId={}", msg.getOrderId(), e);
            channel.basicNack(tag, false, true);
        }
    }

    /** 第一层幂等：Redis SETNX，返回 true 表示重复消息 */
    private boolean isDuplicate(String bizType, String msgId) {
        Boolean first = stringRedisTemplate.opsForValue()
                .setIfAbsent("mq:idem:" + bizType + ":" + msgId, "1", 10, TimeUnit.MINUTES);
        return !Boolean.TRUE.equals(first);
    }
}