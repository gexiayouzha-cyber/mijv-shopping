package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.entity.ProductSku;
import com.gexiayouzha.mijvshopping.mapper.ProductSkuMapper;
import com.gexiayouzha.mijvshopping.service.IProductSkuService;
import org.springframework.stereotype.Service;

@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements IProductSkuService {
}
