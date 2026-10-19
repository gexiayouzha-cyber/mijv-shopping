package com.gexiayouzha.mijvshopping.controller;


import com.gexiayouzha.mijvshopping.DTO.CartAddDTO;
import com.gexiayouzha.mijvshopping.DTO.CartUpdateDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.ICartService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    @PostMapping
    public Result addToCart(@RequestBody CartAddDTO cartAddDTO) {
        return cartService.addToCart(cartAddDTO.getProductId(),
                cartAddDTO.getSkuId(),
                cartAddDTO.getQuantity());
    }

    @GetMapping
    public Result getCartList(){
        Long userId = UserHolder.getUser().getUserId();
        return cartService.getCartList(userId);
    }

    @PutMapping("/{cartId}")
    public Result updateCart(@PathVariable Long cartId,
                             @RequestBody CartUpdateDTO cartUpdateDTO) {
        return cartService.updateCart(cartId,
                cartUpdateDTO.getQuantity(),
                cartUpdateDTO.getIsSelected());
    }

    @DeleteMapping("/{cartId}")
    public Result deleteCart(@PathVariable Long cartId) {
        return cartService.deleteCart(cartId);
    }

}
