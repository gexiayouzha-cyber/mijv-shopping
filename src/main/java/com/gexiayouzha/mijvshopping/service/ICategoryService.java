package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Category;

public interface ICategoryService extends IService<Category> {

    Result getCategories(Integer parentId);
}
