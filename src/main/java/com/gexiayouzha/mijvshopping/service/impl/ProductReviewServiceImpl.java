package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.PageResult;
import com.gexiayouzha.mijvshopping.DTO.ProductReviewDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.ProductReview;
import com.gexiayouzha.mijvshopping.entity.User;
import com.gexiayouzha.mijvshopping.mapper.ProductReviewMapper;
import com.gexiayouzha.mijvshopping.mapper.UserMapper;
import com.gexiayouzha.mijvshopping.service.IProductReviewService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductReviewServiceImpl extends ServiceImpl<ProductReviewMapper, ProductReview> implements IProductReviewService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result listReviews(Integer productId, Integer page, Integer pageSize, Integer rating) {
        //分页查询是查一小部分
        if(page == null || page < 1) {
            page = 1;
        }
        if(pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<ProductReview> pageParam = new Page<>(page, pageSize);


        LambdaQueryWrapper<ProductReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductReview::getProductId, productId);
        if(rating != null && rating >= 1 && rating <= 5) {
            wrapper.eq(ProductReview::getRating, rating);
        }
        wrapper.orderByDesc(ProductReview::getRating);
        //这里的wrapper会查询所有的review
        //核心的分页操作:取要获得的数据
        Page<ProductReview> ReviewPage = page(pageParam, wrapper);

        List<Long> userIds = ReviewPage.getRecords()
                .stream()
                .map(ProductReview::getUserId)
                .distinct()
                .toList();

        Map<Long, String> userNameMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getUserId, User::getUserName));

        List<ProductReviewDTO> list = ReviewPage.getRecords()
                .stream()
                .map(reviewPage -> {
                    ProductReviewDTO productReviewDTO = BeanUtil.copyProperties(reviewPage, ProductReviewDTO.class);
                    String userName = userNameMap.get(reviewPage.getUserId());
                    productReviewDTO.setUsername(userName);
                    return productReviewDTO;
                        }
                )
                .toList();

        PageResult pageResult = new PageResult<ProductReviewDTO>(list, (int) ReviewPage.getTotal(), page, pageSize);
        return Result.success(200,"获取成功", pageResult);
    }
}
