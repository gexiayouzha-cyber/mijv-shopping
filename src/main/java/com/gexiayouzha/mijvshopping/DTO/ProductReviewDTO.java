package com.gexiayouzha.mijvshopping.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductReviewDTO {
    private Long reviewId;
    private Long userId;
    private String username;
    private Integer rating;
    private String reviewContent;
    private String reviewImages;
    private String sellerReply;
    private LocalDateTime createTime;
}
