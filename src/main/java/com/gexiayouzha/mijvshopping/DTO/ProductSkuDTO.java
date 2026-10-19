package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductSkuDTO {
    private Long skuId;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private String skuImageUrl;
    private String skuAttributes;

}
