package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.CouponReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Coupon;

import java.util.List;

public interface ICouponService extends IService<Coupon> {
    Result<List<CouponReturnDTO>> getAvailableCoupons();
    Result receiveCoupon(Long couponId);
}
