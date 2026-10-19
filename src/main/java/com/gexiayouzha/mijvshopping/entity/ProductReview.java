package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_product_review")
public class ProductReview {
    @TableId(value = "id",type=IdType.AUTO)
    private Long reviewId;
    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long userId;
    private Long skuId;
    private Integer rating;
    private Integer ratingContent;
    private Integer ratingService;
    private Integer ratingShipping;
    private String reviewContent;
    private String reviewImages;
    private Integer isAnonymity;
    private Integer isTop;
    private Integer likeCount;
    private Integer dislikeCount;
    private String sellerReply;
    private LocalDateTime sellerReplyTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
