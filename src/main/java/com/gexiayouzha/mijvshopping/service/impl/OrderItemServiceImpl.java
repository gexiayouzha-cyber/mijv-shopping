package com.gexiayouzha.mijvshopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.entity.OrderItem;
import com.gexiayouzha.mijvshopping.mapper.OrderItemMapper;
import com.gexiayouzha.mijvshopping.service.IOrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {
}
