package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.CartListDTO;
import com.gexiayouzha.mijvshopping.DTO.CartReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Product;
import com.gexiayouzha.mijvshopping.entity.ProductSku;
import com.gexiayouzha.mijvshopping.entity.ShoppingCart;
import com.gexiayouzha.mijvshopping.mapper.CartMapper;
import com.gexiayouzha.mijvshopping.mapper.ProductMapper;
import com.gexiayouzha.mijvshopping.mapper.ProductSkuMapper;
import com.gexiayouzha.mijvshopping.service.ICartService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, ShoppingCart> implements ICartService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public Result addToCart(Long productId, Long skuId, Integer quantity) {

        Long userId = UserHolder.getUser().getUserId();
        Product product = productMapper.selectById(productId);
        if(product == null){
            return Result.fail(404, "商品不存在");
        }
        if(skuId == null){
            LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductSku::getProductId, productId)
                    .orderByAsc(ProductSku::getSkuId);
            ProductSku productSku = productSkuMapper.selectOne(queryWrapper);
            skuId = productSku.getSkuId();
        }
        ProductSku productSku = productSkuMapper.selectById(skuId);
        if(productSku == null){
            return Result.fail(404, "SKU不存在");
        }
        Integer stock = productSku.getStock();
        if(stock < quantity){
            return Result.fail(605, "库存不足");
        }
        //查询是否加入过购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getProductId, productId)
                .eq(ShoppingCart::getSkuId, skuId)
                .eq(ShoppingCart::getQuantity,quantity);
        ShoppingCart cart = getOne(wrapper);
        if(cart != null){
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        cart = new ShoppingCart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setSkuId(skuId);
        cart.setQuantity(quantity);
        cart.setIsSelected((byte) 1);
        save(cart);
        CartReturnDTO cartReturnDTO = BeanUtil.copyProperties(cart, CartReturnDTO.class);
        cartReturnDTO.setCartId(cart.getCartId());
        cartReturnDTO.setProductName(product.getProductName());
        cartReturnDTO.setSkuName(productSku.getSkuName());
        cartReturnDTO.setPrice(product.getPrice());
        cartReturnDTO.setImageURL(product.getMainImageURL());

        return Result.success(200, "添加成功", cartReturnDTO);
    }

    @Override
    public Result getCartList(Long userId) {
        List<ShoppingCart> cartList = list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, userId));
        List<CartListDTO> cartDTOList = cartList.stream()
                .map(cart -> BeanUtil.copyProperties(cart, CartListDTO.class))
                .toList();
        return Result.success(200, "获取成功", cartDTOList);
    }

    @Override
    public Result updateCart(Long cartId, Integer quantity, Byte isSelected) {
        Long userId = UserHolder.getUser().getUserId();
        ShoppingCart cart = getById(cartId);
        if(cart == null){
            return Result.fail(404, "购物车记录不存在");
        }
        //不能用!=, 因为不是基本类型, 而是包装类型, !=比较的是引用
        //.equals()比较的是值
        if(!cart.getUserId().equals(userId)){
            return Result.fail(403, "无权限改购物车");
        }
        if(quantity!=null){
            //检查库存足够否
            //cart一定存在, 但它的SkuId不一定存在
            ProductSku productSku = productSkuMapper.selectById(cart.getSkuId());
            if(productSku == null){
                return Result.fail(404, "sku不存在");
            }
            if(productSku.getStock()<quantity){
                return Result.fail(605, "商品库存不足");
            }
            cart.setQuantity(quantity);
        }
        if(isSelected!=null){
            cart.setIsSelected(isSelected);
        }
        updateById(cart);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cartId", cart.getCartId());
        result.put("quantity", cart.getQuantity());
        result.put("isSelected", cart.getIsSelected());
        return Result.success(200, "更新成功", result);
    }

    @Override
    public Result deleteCart(Long cartId) {
        ShoppingCart cart = getById(cartId);
        Long userId = UserHolder.getUser().getUserId();
        //检查购物车记录是否存在
        if(cart == null){
            return Result.fail(404, "购物车记录不存在");
        }
        //检查有没有权限
        if(!cart.getUserId().equals(userId)){
            return Result.fail(403, "无权删除此购物车");
        }
        removeById(cart.getCartId());
        return Result.success(200, "删除成功", null);
    }

}
