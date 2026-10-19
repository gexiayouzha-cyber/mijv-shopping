package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailReturnDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String mainImageURL;
    private List<String> imageURLs;
    private String description;
    private String productDetail;
    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer isFreeShipping;
    private BigDecimal shippingFee;
    private List<ProductAttributeDTO> attributes;
    private List<ProductSkuDTO> skus;
}
