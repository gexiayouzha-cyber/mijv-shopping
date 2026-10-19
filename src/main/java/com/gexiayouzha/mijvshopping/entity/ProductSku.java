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

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_product_sku")
public class ProductSku {
    @TableId(value = "id",type = IdType.AUTO)
    private Long skuId;
    private Long productId;
    private String skuCode;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    @TableField("sku_image_url")
    private String skuImageURL;
    private String skuAttributes;
    private Integer salesCount;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
