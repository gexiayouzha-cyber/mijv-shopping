package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.CouponReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/coupons")
public class CouponController {

    @Autowired
    private ICouponService couponService;

    @GetMapping("/available")
    public Result<List<CouponReturnDTO>> getAvailableCoupons() {
        return couponService.getAvailableCoupons();
    }

    @PostMapping("/{couponId}/receive")
    public Result receiveCoupon(@PathVariable Long couponId) {
        return couponService.receiveCoupon(couponId);
    }
}