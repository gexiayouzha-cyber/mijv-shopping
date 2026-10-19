package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.CouponReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Coupon;
import com.gexiayouzha.mijvshopping.entity.UserCoupon;
import com.gexiayouzha.mijvshopping.mapper.CouponMapper;
import com.gexiayouzha.mijvshopping.mapper.UserCouponMapper;
import com.gexiayouzha.mijvshopping.service.ICouponService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public Result<List<CouponReturnDTO>> getAvailableCoupons() {
        LocalDateTime now = LocalDateTime.now();

        List<Coupon> coupons = lambdaQuery()
                .eq(Coupon::getCouponStatus, "A")
                .lt(Coupon::getStartTime, now)
                .gt(Coupon::getEndTime, now)
                .gt(Coupon::getRemainCount, 0)
                .list();

        List<CouponReturnDTO> result = coupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Result.success(200, "获取成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result receiveCoupon(Long couponId) {
        Long userId = UserHolder.getUser().getUserId();

        Coupon coupon = getById(couponId);
        if (coupon == null) {
            return Result.fail(404, "优惠券不存在");
        }

        if (!"A".equals(coupon.getCouponStatus())) {
            return Result.fail(400, "优惠券已下架");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) {
            return Result.fail(400, "优惠券尚未开始");
        }
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
            return Result.fail(400, "优惠券已过期");
        }

        if (coupon.getRemainCount() <= 0) {
            return Result.fail(400, "优惠券已领完");
        }

        Integer perUserLimit = coupon.getPerUserLimit() != null ? coupon.getPerUserLimit() : 1;
        Long receivedCount = userCouponMapper.selectCount(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserCoupon>lambdaQuery()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId)
        );
        if (receivedCount >= perUserLimit) {
            return Result.fail(400, "已达领取上限");
        }

        // 原子操作扣减库存：UPDATE tb_coupon SET remain_count = remain_count - 1 WHERE id = ? AND remain_count > 0
        int rows = baseMapper.update(null,
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Coupon>lambdaUpdate()
                        .setSql("remain_count = remain_count - 1")
                        .eq(Coupon::getCouponId, couponId)
                        .gt(Coupon::getRemainCount, 0)
        );
        if (rows == 0) {
            return Result.fail(400, "优惠券已领完");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponStatus("U");
        userCoupon.setReceivedTime(now);
        userCoupon.setExpireTime(coupon.getEndTime());
        userCouponMapper.insert(userCoupon);

        return Result.success(200, "领取成功", null);
    }

    private CouponReturnDTO convertToDTO(Coupon coupon) {
        CouponReturnDTO dto = new CouponReturnDTO();
        dto.setCouponId(coupon.getCouponId());
        dto.setCouponName(coupon.getCouponName());
        dto.setCouponType(coupon.getCouponType());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinOrderAmount(coupon.getMinOrderAmount());
        dto.setExpireTime(coupon.getEndTime());
        return dto;
    }
}
