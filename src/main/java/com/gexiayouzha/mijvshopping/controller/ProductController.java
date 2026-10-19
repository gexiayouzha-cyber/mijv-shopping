package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
    @Autowired
    private IProductService productService;

    @GetMapping
    public Result listProducts(@RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String order) {
        return productService.listProducts(categoryId,page,pageSize,sort,order);
    }

    @GetMapping("/{productId}")
    public Result getProductById(@PathVariable Integer productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/search")
    public Result searchProducts(@RequestParam String keyword,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer pageSize) {
        return productService.searchProducts(keyword, page, pageSize);
    }

    @GetMapping("/hot")
    public Result getHotProducts(){
        return productService.getHotProducts();
    }


}
