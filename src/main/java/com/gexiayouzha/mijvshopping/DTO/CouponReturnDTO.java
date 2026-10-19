package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponReturnDTO {
    private Long couponId;
    private String couponName;
    private String couponType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private LocalDateTime expireTime;
}