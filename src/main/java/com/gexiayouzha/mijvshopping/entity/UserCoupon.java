package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user_coupon")
public class UserCoupon {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long couponId;

    private String couponStatus;

    private Long orderId;

    private LocalDateTime receivedTime;

    private LocalDateTime usedTime;

    private LocalDateTime expireTime;
}