package com.gexiayouzha.mijvshopping.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductReturnDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImageURL;
    //private String imageURLs;这从哪来的?!
    private Integer salesCount;
    private Integer stock;

}
