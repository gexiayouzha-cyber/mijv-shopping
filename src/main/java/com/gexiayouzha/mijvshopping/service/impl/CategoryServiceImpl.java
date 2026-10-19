package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.CategoryDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Category;
import com.gexiayouzha.mijvshopping.mapper.CategoryMapper;
import com.gexiayouzha.mijvshopping.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Result getCategories(Integer parentId) {
        String key = "category:list:"+(parentId == null? "root":parentId);
        Object cached = redisTemplate.opsForValue().get(key);
        if(cached instanceof List<?> list ){
            return Result.success(200, "获取成功", list);
        }
        //我们希望的是, 如果不传parent_id, 则显示的是大类, 也就是parent_id为null的对象
        //但parent_id为null时
        //MyBatis-Plus 会 ：自动忽略这个条件，查询 所有记录 ！
        List<Category> categoryList = new ArrayList<>();
        if(parentId == null){
            categoryList = query().isNull("parent_id")
                    .orderByAsc("sort_order")
                    .list();
        }
        else {
            categoryList = query().eq("parent_id", parentId)
                    .orderByAsc("sort_order")
                    .list();

        }
        List<CategoryDTO> categoryDTOList = categoryList.stream()
                .map(category -> BeanUtil.copyProperties(category, CategoryDTO.class))
                .toList();
        redisTemplate.opsForValue().set(key, categoryDTOList, 2, TimeUnit.HOURS);
        return Result.success(200, "获取成功", categoryDTOList);
    }
}
