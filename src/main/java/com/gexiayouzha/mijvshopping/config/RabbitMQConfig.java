package com.gexiayouzha.mijvshopping.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE   = "order.event.exchange";
    public static final String QUEUE_CREATE    = "order.create.queue";
    public static final String QUEUE_PAID      = "order.paid.queue";
    public static final String QUEUE_DELAY     = "order.delay.queue";
    public static final String QUEUE_CLOSE     = "order.close.queue";

    public static final String RK_CREATE = "order.create";
    public static final String RK_PAID   = "order.paid";
    public static final String RK_DELAY  = "order.delay";
    public static final String RK_CLOSE  = "order.close";

    /** 30分钟，与订单 paymentDeadline 口径一致 */
    public static final int ORDER_TTL = 30 * 60 * 1000;

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // 必须声明信任包，否则消费端反序列化报错
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.gexiayouzha.mijvshopping.mq");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public TopicExchange orderExchange() {
        return ExchangeBuilder.topicExchange(ORDER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(QUEUE_CREATE).build();
    }

    @Bean
    public Queue orderPaidQueue() {
        return QueueBuilder.durable(QUEUE_PAID).build();
    }

    @Bean
    public Queue orderCloseQueue() {
        return QueueBuilder.durable(QUEUE_CLOSE).build();
    }

    // 延迟队列：消息存活30分钟后变死信，投递给本交换机的 order.close
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(QUEUE_DELAY)//创建一个队列, 参数是队列名称
                .withArgument("x-message-ttl", ORDER_TTL)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)//第一个参数:参数名(固定, 不能随便写), 第二个参数: 死信交换机, 消息过期了, 不直接删掉, 而是发送到名叫ORDER_EXCHANGE的死信交换机里
                .withArgument("x-dead-letter-routing-key", RK_CLOSE)//死信发送的路由键
                .build();
    }

    @Bean
    public Binding createBinding() {
        return BindingBuilder.bind(orderCreateQueue()).to(orderExchange()).with(RK_CREATE);
    }//这个就是在做 “交换机和队列之间的绑定”。你可以把它理解成：
    //告诉 RabbitMQ：如果消息的路由键是 RK_CREATE，就把它送到 orderCreateQueue()。

    @Bean
    public Binding paidBinding() {
        return BindingBuilder.bind(orderPaidQueue()).to(orderExchange()).with(RK_PAID);
    }

    @Bean
    public Binding closeBinding() {
        return BindingBuilder.bind(orderCloseQueue()).to(orderExchange()).with(RK_CLOSE);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderExchange()).with(RK_DELAY);
    }
}