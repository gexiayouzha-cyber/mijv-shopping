package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.controller.ProductReviewController;
import com.gexiayouzha.mijvshopping.entity.ProductReview;

public interface IProductReviewService extends IService<ProductReview> {
    Result listReviews(Integer productId, Integer page, Integer pageSize, Integer rating);

}
