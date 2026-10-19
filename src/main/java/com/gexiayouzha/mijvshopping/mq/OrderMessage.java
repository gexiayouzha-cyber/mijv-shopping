package com.gexiayouzha.mijvshopping.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {
    private String msgId;       // 消息唯一ID，用于幂等
    private Long orderId;
    private String orderNumber;
    private Long userId;
}