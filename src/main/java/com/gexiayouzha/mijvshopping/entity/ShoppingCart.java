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
@TableName("tb_shopping_cart")
public class ShoppingCart {
    @TableId(value = "id", type = IdType.AUTO)
    private Long cartId;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private Byte isSelected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
