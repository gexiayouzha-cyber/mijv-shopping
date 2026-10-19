package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.*;
import com.gexiayouzha.mijvshopping.entity.Product;
import com.gexiayouzha.mijvshopping.entity.ProductAttribute;
import com.gexiayouzha.mijvshopping.entity.ProductSku;
import com.gexiayouzha.mijvshopping.mapper.ProductAttributeMapper;
import com.gexiayouzha.mijvshopping.mapper.ProductMapper;
import com.gexiayouzha.mijvshopping.mapper.ProductSkuMapper;
import com.gexiayouzha.mijvshopping.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
    @Autowired
    private ProductAttributeMapper attributeMapper;
    @Autowired
    private ProductSkuMapper skuMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Result listProducts(Integer categoryId, Integer page, Integer pageSize, String sort, String order) {
        if(page == null || page < 1) {
            page = 1;
        }
        if(pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        //sort是告诉你按照什么排序
        if(sort==null){
            sort = "createTime";
        }
        if(order==null){
            order = "desc";
        }
        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if(categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if("createTime".equals(sort)) {//第二个参数, 如果是true, 就是升序; 反之, 降序
            wrapper.orderBy(true, "asc".equals(order), Product::getCreateTime);
        }else if("price".equals(sort)) {
            wrapper.orderBy(true, "asc".equals(order), Product::getPrice);
        }else{//salesCount
            wrapper.orderBy(true, "asc".equals(order), Product::getSalesCount);
        }

        Page<Product> result = page(pageParam, wrapper);
        List<ProductReturnDTO> list = result.getRecords().stream().map(product ->
                BeanUtil.copyProperties(product, ProductReturnDTO.class)
        ).toList();

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", result.getTotal());
        data.put("pageSize", result.getSize());
        data.put("page", result.getCurrent());

        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result getProductById(Integer productId) {
        //新增
        String key = "product:detail:" + productId;
        Object cached = redisTemplate.opsForValue().get(key);
        if(cached!=null){
            if(cached instanceof ProductDetailReturnDTO){
                ProductDetailReturnDTO dto = (ProductDetailReturnDTO) cached;
                return Result.success(200, "获取成功", dto);
            }
            //这种情况是cached=="",为啥不把外面这个if去掉, 下面这个return和下下面的return.fail合并?
            //因为下下面要查数据库, 而这里还没查数据库, 查的是redis
            return Result.fail(404,"商品不存在");
        }


        Product product = getById(productId);
        if(product == null) {
            //空值保存5min, 防止有人恶意攻击数据库, 访问不存在的值
            //redisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
            return Result.fail(404, "商品不存在");
        }

        LambdaQueryWrapper<ProductAttribute> attWrapper = new LambdaQueryWrapper<>();
        attWrapper.eq(ProductAttribute::getProductId, productId)
                .orderByAsc(ProductAttribute::getSortOrder);
        List<ProductAttribute> attributes = attributeMapper.selectList(attWrapper);
        List<ProductAttributeDTO> attList = attributes.stream()
                .map(attribute -> BeanUtil.copyProperties(attribute, ProductAttributeDTO.class))
                .toList();

        LambdaQueryWrapper<ProductSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(ProductSku::getProductId, productId);
        List<ProductSku> skus = skuMapper.selectList(skuWrapper);
        List<ProductSkuDTO> skuList = skus.stream()
                .map(sku -> BeanUtil.copyProperties(sku, ProductSkuDTO.class))
                .toList();

        ProductDetailReturnDTO productDetailReturnDTO = BeanUtil.copyProperties(product, ProductDetailReturnDTO.class);
        productDetailReturnDTO.setSkus(skuList);
        productDetailReturnDTO.setAttributes(attList);
        //return前写回缓存, 设置失效随机值, 防止缓存雪崩
        //避免大量key同一时刻同时失效
        long ttl = 30 + ThreadLocalRandom.current().nextLong(10);
        redisTemplate.opsForValue().set(key, productDetailReturnDTO, ttl, TimeUnit.MINUTES);
        return Result.success(200,"获取成功", productDetailReturnDTO);
    }

    @Override
    public Result searchProducts(String keyword, Integer page, Integer pageSize) {
        if(page == null || page < 1) {
            page = 1;
        }
        if(pageSize ==null || pageSize < 1) {
            pageSize = 10;
        }
        Page<Product> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if(keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w->w.like(Product::getProductName, keyword)
                        .or()
                        .like(Product::getDescription, keyword)
            );
        }
        Page<Product> productPage = page(pageParam, wrapper);
        List<ProductReturnDTO> productDTOList = productPage.getRecords()
                .stream()
                .map(product -> BeanUtil.copyProperties(product, ProductReturnDTO.class))
                .toList();
        PageResult<ProductReturnDTO> result = new PageResult<>(productDTOList, (int) productPage.getTotal(), page, pageSize);
        return Result.success(200, "获取成功", result);
    }

    @Override
    public Result getHotProducts() {
        String rankKey = "rank:sales";

        // 1. 从 ZSet 取 Top10 (按分数降序)
        Set<ZSetOperations.TypedTuple<Object>> top10 =
                redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, 0, 9);

        // 2. ZSet 空的(还没人下单确认过),返回空列表
        if (top10 == null || top10.isEmpty()) {
            return Result.success(200, "获取成功", Collections.emptyList());
        }

        // 3. 取出 productId 列表 + 分数映射
        //    ZSet 的 member 是 String("1"), 要转回 Long 才能查库
        List<Long> productIds = new ArrayList<>();
        Map<Long, Double> scoreMap = new HashMap<>();
        for (ZSetOperations.TypedTuple<Object> tuple : top10) {
            Long pid = Long.parseLong(tuple.getValue().toString());
            productIds.add(pid);
            scoreMap.put(pid, tuple.getScore());
        }

        // 4. 批量查商品信息 (用 listByIds, 你 ServiceImpl 继承的方法)
        List<Product> products = listByIds(productIds);

        // 5. 转成 DTO 并按 ZSet 分数排序
        //    你项目已有 ProductReturnDTO, 这里直接用
        List<ProductReturnDTO> result = products.stream()
                .map(p -> BeanUtil.copyProperties(p, ProductReturnDTO.class))
                // 按 ZSet 的分数(销量)降序排
                .sorted((a, b) -> Double.compare(
                        scoreMap.getOrDefault(b.getProductId(), 0.0),
                        scoreMap.getOrDefault(a.getProductId(), 0.0)
                ))
                .toList();

        return Result.success(200, "获取成功", result);
    }
}
