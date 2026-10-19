package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@TableName("tb_payment")
public class Payment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long paymentId;

    private String paymentNumber;

    private Long orderId;

    private Long userId;

    private String paymentMethod;

    private String paymentStatus;

    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    private String transactionId;

    private LocalDateTime paymentTime;

    private LocalDateTime notifyTime;

    private String notifyData;

    @TableField("pay_channel_trade_no")
    private String payChannelTradeNo;

    private BigDecimal refundAmount;

    private LocalDateTime refundTime;

    private String refundReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}