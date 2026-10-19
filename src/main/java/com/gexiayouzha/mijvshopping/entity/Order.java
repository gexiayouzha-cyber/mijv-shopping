package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("tb_order")
public class Order {
    @TableId(value = "id", type = IdType.AUTO)
    private Long orderId;

    private String orderNumber;

    private Long userId;

    private Long addressId;

    private String orderStatus;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal shippingFee;

    private BigDecimal actualPaidAmount;

    private Integer useBonusPoints;

    private BigDecimal bonusPointsDiscount;

    private Long couponId;

    private BigDecimal couponDiscount;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private LocalDateTime paymentDeadline;

    private String shippingMethod;

    private String trackingNumber;

    private LocalDateTime shippingTime;

    private LocalDateTime receiveTime;

    private LocalDateTime confirmReceiveTime;

    private String orderRemarks;

    private String customerServiceRemarks;

    private String cancelReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime closeTime;
}
