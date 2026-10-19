package com.gexiayouzha.mijvshopping.entity;


import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableName("tb_coupon")
    public class Coupon {
        @TableId(value = "id", type = IdType.AUTO)
        private Long couponId;

        private String couponCode;

        private String couponName;

        private String couponType;

        private BigDecimal discountValue;

        private BigDecimal minOrderAmount;

        private BigDecimal maxDiscountAmount;

        private Integer totalCount;

        private Integer remainCount;

        private Integer perUserLimit;

        private String applicableProductType;

        private String applicableProductIds;

        private String applicableCategoryIds;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private String couponStatus;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;

}
