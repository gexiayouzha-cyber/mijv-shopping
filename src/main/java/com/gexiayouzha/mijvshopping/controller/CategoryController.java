package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public Result getCategories(@RequestParam(required = false) Integer parentId) {
        return categoryService.getCategories(parentId);
    }
}
