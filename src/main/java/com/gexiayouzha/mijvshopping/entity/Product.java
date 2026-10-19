package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_product")
public class Product {
    //AI生成的属性(从数据库提取的)
    @TableId(value = "id", type = IdType.AUTO)
    private Long productId;
    private String productName;
    private String productCode;
    private Integer categoryId;
    //卖家Id
    private Long sellerId;
    //高精度小数, 用于价格
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;//成本价
    private Integer stock;
    private Integer stockAlertThreshold;
    private String unit;
    private String productStatus;
    @TableField(value = "main_image_url")
    private String mainImageURL;
    @TableField("image_urls")
    private String imageURLs;
    private String description;

    // 来自第一张图
    private String productDetail;
    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer isFreeShipping;
    private BigDecimal shippingFee;
    private BigDecimal weight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime onShelfTime;
    private LocalDateTime offShelfTime;

}
