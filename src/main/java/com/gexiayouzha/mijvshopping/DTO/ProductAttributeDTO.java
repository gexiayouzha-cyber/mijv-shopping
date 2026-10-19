package com.gexiayouzha.mijvshopping.DTO;

import com.gexiayouzha.mijvshopping.entity.ProductAttribute;
import com.gexiayouzha.mijvshopping.entity.ProductSku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProductAttributeDTO {
    private String attributeName;
    private String attributeValue;
}
