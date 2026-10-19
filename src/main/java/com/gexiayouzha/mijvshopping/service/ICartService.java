package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.ShoppingCart;

public interface ICartService extends IService<ShoppingCart> {
    Result addToCart(Long productId, Long skuId, Integer quantity);

    Result getCartList(Long userId);

    Result updateCart(Long cartId, Integer quantity, Byte isSelected);

    Result deleteCart(Long cartId);
}
