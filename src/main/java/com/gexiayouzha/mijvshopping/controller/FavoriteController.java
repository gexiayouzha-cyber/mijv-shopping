package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.FavoriteReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/favorites")
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    @PostMapping
    public Result addFavorite(@RequestBody AddFavoriteRequest request) {
        return favoriteService.addFavorite(request.getProductId());
    }

    @GetMapping
    public Result getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        FavoriteReturnDTO data = favoriteService.getFavorites(page, pageSize);
        return Result.success(200, "获取成功", data);
    }

    @DeleteMapping("/{productId}")
    public Result removeFavorite(
            @PathVariable Integer productId
    ){
        return favoriteService.removeFavorite(productId);
    }

    @lombok.Data
    public static class AddFavoriteRequest {
        @com.fasterxml.jackson.annotation.JsonProperty("productID")
        @com.fasterxml.jackson.annotation.JsonAlias("productId")
        private Long productId;
    }
}
