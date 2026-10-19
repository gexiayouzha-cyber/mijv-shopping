package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products/{productId}/reviews")
public class ProductReviewController {
    @Autowired
    private IProductReviewService productReviewService;
    @GetMapping
    public Result listReviews(@PathVariable Integer productId,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize,
                              @RequestParam(required = false) Integer rating) {
        return productReviewService.listReviews(productId, page, pageSize, rating);
    }

}
