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
@TableName("tb_order_item")
public class OrderItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Long orderItemId;

    private Long orderId;

    private Long productId;

    private Long skuId;

    private String productName;

    private String skuName;

    @TableField("product_image_url")
    private String productImageURL;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private Integer isCommented;

    private LocalDateTime createTime;

}
