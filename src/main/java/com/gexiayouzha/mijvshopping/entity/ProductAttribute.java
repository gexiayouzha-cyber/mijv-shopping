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
@TableName("tb_product_attribute")
public class ProductAttribute {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String attributeName;
    private String attributeValue;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
