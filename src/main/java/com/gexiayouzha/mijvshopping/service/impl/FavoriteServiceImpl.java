package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.FavoriteReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Favorite;
import com.gexiayouzha.mijvshopping.entity.Product;
import com.gexiayouzha.mijvshopping.mapper.FavoriteMapper;
import com.gexiayouzha.mijvshopping.service.IFavoriteService;
import com.gexiayouzha.mijvshopping.service.IProductService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

    @Autowired
    private IProductService productService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result addFavorite(Long productId) {
        if (productId == null) {
            return Result.fail(400, "商品ID不能为空");
        }

        Product product = productService.getById(productId);
        if (product == null) {
            return Result.fail(404, "商品不存在");
        }

        Long userId = UserHolder.getUser().getUserId();

        Long existCount = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .count();
        if (existCount > 0) {
            return Result.fail(400, "已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        save(favorite);

        return Result.success(200, "收藏成功", null);
    }

    @Override
    public FavoriteReturnDTO getFavorites(Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Long userId = UserHolder.getUser().getUserId();

        Page<Favorite> favoritePage = lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime)
                .page(new Page<>(page, pageSize));

        List<Favorite> favorites = favoritePage.getRecords();

        List<FavoriteReturnDTO.FavoriteItemDTO> itemList = favorites.stream().map(favorite -> {
            Product product = productService.getById(favorite.getProductId());
            if (product == null) {
                return null;
            }
            FavoriteReturnDTO.FavoriteItemDTO item = new FavoriteReturnDTO.FavoriteItemDTO();
            item.setFavoriteId(favorite.getId());
            item.setProductId(favorite.getProductId());
            item.setProductName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setMainImageURL(product.getMainImageURL());
            item.setCreateTime(favorite.getCreateTime() != null ? favorite.getCreateTime().format(FORMATTER) : null);
            return item;
        }).filter(item -> item != null).collect(Collectors.toList());

        FavoriteReturnDTO result = new FavoriteReturnDTO();
        result.setList(itemList);
        result.setTotal(favoritePage.getTotal());
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public Result removeFavorite(Integer productId) {
        if(productId==null){
            return Result.fail(400, "商品id不能为空");
        }
        Long userId = UserHolder.getUser().getUserId();
        boolean removed = lambdaUpdate()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId)
                .remove();
        if(!removed){
            return Result.fail(404, "未收藏该商品");
        }
        return Result.success(200, "取消成功", null);

    }
}
