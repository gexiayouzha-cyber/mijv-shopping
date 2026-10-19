package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartReturnDTO {
    private Long cartId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private String productName;
    private String skuName;
    private BigDecimal price;
    private String imageURL;
}
