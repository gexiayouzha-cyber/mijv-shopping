package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Product;

public interface IProductService extends IService<Product> {
    Result listProducts(Integer categoryId, Integer page, Integer pageSize, String sort, String order);

    Result getProductById(Integer productId);


    Result searchProducts(String keyword, Integer page, Integer pageSize);

    Result getHotProducts();

}
