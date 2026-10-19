package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.FavoriteReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Favorite;

public interface IFavoriteService extends IService<Favorite> {
    Result addFavorite(Long productId);

    FavoriteReturnDTO getFavorites(Integer page, Integer pageSize);

    Result removeFavorite(Integer productId);
}
