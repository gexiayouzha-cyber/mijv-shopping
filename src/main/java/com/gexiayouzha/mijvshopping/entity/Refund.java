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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_refund")
public class Refund {
    @TableId(value = "id", type = IdType.AUTO)
    private Long refundId;

    private String refundNumber;

    private Long orderId;

    private Long orderItemId;

    private Long userId;

    private String refundType;

    private String refundReason;

    private BigDecimal refundAmount;

    private String refundStatus;

    private String transactionId;

    private BigDecimal actualRefundAmount;

    private LocalDateTime refundTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}