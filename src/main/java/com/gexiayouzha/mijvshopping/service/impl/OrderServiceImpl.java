package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.LogisticsReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.OrderDetailDTO;
import com.gexiayouzha.mijvshopping.DTO.OrderItemDTO;
import com.gexiayouzha.mijvshopping.DTO.OrderReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.*;
import com.gexiayouzha.mijvshopping.mapper.OrderMapper;
import com.gexiayouzha.mijvshopping.mapper.ProductSkuMapper;
import com.gexiayouzha.mijvshopping.mq.OrderMQProducer;
import com.gexiayouzha.mijvshopping.service.*;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.random;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private IAddressService addressService;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IProductSkuService productSkuService;//why???
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private ILogisticsCompanyService logisticsCompanyService;
    @Autowired
    private ILogisticsDetailService logisticsDetailService;
    @Autowired
    private ILogisticsTrackingService logisticsTrackingService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderMQProducer orderMQProducer;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final DefaultRedisScript<Long> STOCK_DECR_SCRIPT;
    static{
        STOCK_DECR_SCRIPT = new DefaultRedisScript<>();
        STOCK_DECR_SCRIPT.setLocation(new ClassPathResource("lua/stock_decr.lua"));
        STOCK_DECR_SCRIPT.setResultType(Long.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result addOrder(String idempotencyKey ,Long addressId, List<Long> cartIds, List<OrderItemDTO> items, Long couponId, Integer useBonusPoints, String orderRemarks) {
        //OrderItemDTO里有productId;skuId;quantity;并且其实productId可以根据skuId查询
        //如果用OrderItemDTO传入数据,后面有没有操作数据库来使得OrderItem的属性填充完整(有了, 181行开始)
        //1.检查地址
        Address address = addressService.getById(addressId);
        if(address == null){
            return Result.fail(404, "地址不存在");
        }
        //2.查询商品(从购物车或直接传入)
        //cartList的每个对象都是ShoppingCart类型, 就是每个对象都是购物车商品的形式存在
        List<ShoppingCart> cartList;//cartList是购物车, 传购物车里的商品Id或者直接传入 商品列表
        //这个if-else说明如果cartIds和items都传入了的话, 查cartIds(购物车商品ID列表)
        Long userId = UserHolder.getUser().getUserId();

        String idempotentKey = null;
        if(idempotencyKey!=null && !idempotencyKey.isEmpty()){
            idempotentKey = "idempotent:order:" + userId + ":" + idempotencyKey;
            final String finalKey = idempotentKey;
            Boolean first = stringRedisTemplate.opsForValue().setIfAbsent(idempotentKey, "processing", Duration.ofMinutes(10));
            if (Boolean.FALSE.equals(first)) {
                // 抢占失败，先看 key 里现在存的是什么
                String value = stringRedisTemplate.opsForValue().get(idempotentKey);
                if ("processing".equals(value)) {
                    // 第一次还在处理中
                    return Result.fail(429, "订单处理中，请勿重复提交");
                }
                // 第一次已完成，value 是结果 JSON，直接返回
                return JSON.parseObject(value, Result.class);
            }

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override
                public void afterCompletion(int status){
                    if(status==STATUS_ROLLED_BACK){
                        stringRedisTemplate.delete(finalKey);
                    }
                }
            });
        }
        if (cartIds != null && !cartIds.isEmpty()) {
            cartList = cartService.lambdaQuery()
                    .in(ShoppingCart::getCartId, cartIds)
                    .eq(ShoppingCart::getUserId ,userId)
                    .list();
            if(cartList.size() != cartIds.size()){
                return Result.fail(404, "部分购物车商品不存在或无权操作");
            }

        } else if (items != null && !items.isEmpty()) {
            // 处理直接传入的商品
            //productId,skuId(),quantity
            //把items里的每一个对象转换成cart, 这样可以传入cartList
            cartList = items.stream()
                    .map(item->{
                        ShoppingCart cart = new ShoppingCart();
                        cart.setProductId(item.getProductId());
                        cart.setQuantity(item.getQuantity());
                        //如果items里没传skuId, 直接选一个商品的类型(数据库里SkuId顺序)
                        Long skuId = item.getSkuId();
                        if(skuId == null){
                            LambdaQueryWrapper<ProductSku> skuWrapper = new LambdaQueryWrapper<>();
                            //前面一个ProductSku::getProductId是数据库的数据,item.getProductId()是前端传入的数据
                            skuWrapper.eq(ProductSku::getProductId, item.getProductId())
                                    .orderByAsc(ProductSku::getSkuId)
                                    .last("limit 1");
                            ProductSku productSku = productSkuService.getOne(skuWrapper);
                            if(productSku == null){
                                throw new RuntimeException("商品规格不存在");
                            }
                            skuId = productSku.getSkuId();
                        }
                        cart.setSkuId(skuId);
                        return cart;
                    })
                    .collect(Collectors.toList());

        } else {
            return Result.fail(400, "购物车ID或商品列表必须传一个");
        }
        //cartList是自己创建的(ShoppingCart类型)
        //根据前端传的cartIds查询的, 或者根据前端传入商品items(是一个列表, 类型是这次创建的OrderItem), 把它
        //cartList的意思最终确定要交付订单的商品

        //3.检查库存+计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Long> skuIdSet = cartList.stream()
                .map(ShoppingCart::getSkuId)
                .collect(Collectors.toSet());
        List<ProductSku> skuIdList = productSkuService.listByIds(skuIdSet);
        Map<Long, ProductSku> skuMap = skuIdList.stream()
                .collect(Collectors.toMap(ProductSku::getSkuId, p -> p));

        for(ShoppingCart cart : cartList){
            //每一个商品cart的skuId来查找到sku, 来找到stock, 来判断库存是否充足
            ProductSku sku = skuMap.get(cart.getSkuId());
            if(sku == null || sku.getStock() < cart.getQuantity()){
                return Result.fail(605, "库存不足");
            }
            totalAmount = totalAmount.add(sku.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }
        //4.使用优惠券
        //discountAmount是总优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        if(couponId != null){
            Coupon coupon = couponService.getById(couponId);
            //优惠券是否存在
            if(coupon == null){
                return Result.fail(404, "优惠券不存在");
            }
            //优惠券是否有效
            if(!"A".equals(coupon.getCouponStatus())){
                return Result.fail(400, "优惠券已停用");
            }
            //优惠券是否在有效时间内
            LocalDateTime now = LocalDateTime.now();
            if(now.isBefore(coupon.getStartTime())){
                return Result.fail(400, "优惠券尚未生效");
            }
            if(now.isAfter(coupon.getEndTime())){
                return Result.fail(608, "优惠券已过期");
            }
            //优惠券是否足够
            if(coupon.getRemainCount()<=0){
                return Result.fail(605, "优惠券已抢完");
            }
            //优惠券使用条件
            if(coupon.getMinOrderAmount().compareTo(totalAmount)>0){
                return Result.fail(400, "订单金额未达到优惠券使用门槛");
            }
            //下面用到的couponType==null
            //和coupon.getDiscountValue()==null有没有可能
            //不可能, 因为设置了数据库里not null
            if("D".equals(coupon.getCouponType())){
                couponDiscountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountValue()));
            } else if ("F".equals(coupon.getCouponType())) {
                couponDiscountAmount = coupon.getDiscountValue();
            }

            //getMaxDiscountAmount==null
            if(coupon.getMaxDiscountAmount()!=null && coupon.getMaxDiscountAmount().compareTo(totalAmount)>0){
                couponDiscountAmount = coupon.getMaxDiscountAmount();
            }
            //扣减库存
            coupon.setRemainCount(coupon.getRemainCount()-1);
            couponService.updateById(coupon);

            boolean update = couponService.lambdaUpdate()
                    .setSql("remain_count = remain_count -1")
                    .eq(Coupon::getCouponId, couponId)
                    .ge(Coupon::getRemainCount, 0)
                    .update();
            if(!update){
                return Result.fail(605, "优惠券已抢完");
            }
        }
        //5.使用积分
        //TODO

        //6.创建订单
        Order order = new Order();
        //TODO 创建订单号的方法
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setOrderStatus("P");
        order.setTotalAmount(totalAmount);
        //TODO这里的discountAmount应该包含积分的优惠
        order.setDiscountAmount(discountAmount);
        //运费, 暂时先0吧
        order.setShippingFee(BigDecimal.ZERO);
        order.setActualPaidAmount(totalAmount.subtract(discountAmount));
        //TODO别急, 积分先不搞
        //order.setUseBonusPoints();
        order.setCouponId(couponId);
        order.setOrderRemarks(orderRemarks);
        order.setCouponDiscount(couponDiscountAmount);

        //TODO超过了30min会怎么样
        order.setPaymentDeadline(LocalDateTime.now().plusMinutes(30));
        //order.setShippingMethod()
        //order.setTrackingNumber()
        //order.setShippingTime()
        //order.setReceivingTime()
        //order.setConfirmReceiveTime()
        order.setOrderRemarks(orderRemarks);
        //order.setCustomerServiceRemarks()
        //order.setCancelReason()
        //order.setCloseTime()//订单被取消的时间, 可能是用户手动取消的
        save(order);

        //7.创建订单项
        //获取所有购物车商品Id
        Set<Long> productIdSet = cartList.stream()
                .map(ShoppingCart::getProductId)//这里map()里填的是方法引用, 等价于cart->cart.getProductId()
                .collect(Collectors.toSet());
        //根据id列表批量查询得到product
        List<Product> productList = productService.listByIds(productIdSet);
        //将id和product做一个映射
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));
        //这里的第一个p是指代productList里的每一个元素, 第二个p是指仍然变成p

    List<long[]> redisDeducted = new ArrayList<>();
    for (ShoppingCart cart : cartList) {
        //orderItem是用户下单的商品以及此单的属性
        //下面的操作成立的原因是OrderItem里很多属性都是ShoppingCart里有的
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setProductId(cart.getProductId());
        orderItem.setSkuId(cart.getSkuId());
        orderItem.setQuantity(cart.getQuantity());
        //TODO单个订单项优惠怎么计算
        //orderItem.setDiscountAmount(discountAmount);

        //获取product的Id, 进而获取product的name和mainImageUrl
        Product product = productMap.get(cart.getProductId());
        //下面这两个if和上面几行一样, 都是为了给orderItem赋值
        if (product != null) {
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImageURL(product.getMainImageURL());
        }
        //ProductSku sku = productSkuService.getById(cart.getSkuId());
        ProductSku sku = skuMap.get(cart.getSkuId());
        if (sku != null) {
            String stockKey = "stock:sku:" + sku.getSkuId();
// key 不存在时, 用数据库当前库存初始化; 已存在则不覆盖(Redis 库存以它自己为准)
            stringRedisTemplate.opsForValue()
                    .setIfAbsent(stockKey, String.valueOf(sku.getStock()));
            Long remain = stringRedisTemplate.execute(
                    STOCK_DECR_SCRIPT,
                    Collections.singletonList(stockKey),//传给lua的keys
                    String.valueOf(cart.getQuantity())//传给lua的args, 要扣减的量
            );
            if (remain == null || remain < 0) {
                throw new RuntimeException("库存不足");
            }
            redisDeducted.add(new long[]{sku.getSkuId(), cart.getQuantity()});

            orderItem.setSkuName(sku.getSkuName());
            orderItem.setUnitPrice(sku.getPrice());
            //orderItem.setTotalAmount(sku.getPrice().multiply(new BigDecimal(cart.getQuantity())));

            //isCommented这个字段默认是0(用户还没评价该商品), 其他orderitem字段均已填完
            //不用算优惠的价格 //???
            //
            boolean success = productSkuService.lambdaUpdate()
                    .setSql("stock = stock - " + cart.getQuantity())
                    .eq(ProductSku::getSkuId, sku.getSkuId())
                    .ge(ProductSku::getStock, cart.getQuantity())
                    .update();
            //由于这里.ge()判断库存是否大于需求数量 与 数据库扣库存是原子性的, 中间不会突然说 某个线程 查询数据时候的数据 和 扣减库存时候数据库的数据 不符
            if (!success) {
                throw new RuntimeException("库存不足");
            }
        }
        orderItemService.save(orderItem);
    }

// 事务最终回滚时，补偿所有已扣的 Redis 库存
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    for (long[] d : redisDeducted) {
                        stringRedisTemplate.opsForValue()
                                .increment("stock:sku:" + d[0], d[1]);
                    }
                }
            }
        });


        //8.清理购物车
        if(cartIds!= null && !cartIds.isEmpty()){
            cartService.removeByIds(cartIds);
        }

        //9.返回结果
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("orderNumber", order.getOrderNumber());
        result.put("totalAmount", order.getTotalAmount());
        result.put("discountAmount", order.getDiscountAmount());
        result.put("actualPaidAmount", order.getActualPaidAmount());
        result.put("paymentDeadline", order.getPaymentDeadline());

        // 事务提交成功后再发MQ，避免"事务回滚了但消息已发"
        //好像只要放在return之前, 这个方法里面的任何位置都行
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderMQProducer.sendOrderCreated(order);
            }
        });
        if(idempotentKey != null){
            stringRedisTemplate.opsForValue().set(idempotentKey,
                    JSONUtil.toJsonStr(result), Duration.ofMinutes(10));
        }
        return Result.success(200, "创建成功", result);


    }

    @Override
    public Result listOrder(String status, Integer page, Integer pageSize) {
        Long userId = UserHolder.getUser().getUserId();

        //if(userId == null){}不用, 因为有登陆拦截器
        if(page == null || page < 1){
            page = 1;
        }
        if(pageSize == null || pageSize < 1){
            pageSize = 10;
        }
        Page<Order> result = query()
                .eq("user_id", userId)
                .eq(status != null, "status", status)
                .page(new Page<>(page, pageSize));
        List<OrderReturnDTO> list = result.getRecords()
                .stream()
                .map(order -> BeanUtil.copyProperties(order, OrderReturnDTO.class))
                .toList();
        LinkedHashMap<String, Object> data1 = new LinkedHashMap<>();
        //这里data1的名字不会显示到返回结果, 而是Result类里的第三个参数的名字data会返回到最终结果
        data1.put("list", list);
        data1.put("total", result.getTotal());
        data1.put("page", page);
        data1.put("pageSize", pageSize);
        return Result.success(200, "获取成功", data1);
    }

    @Override
    public Result getOrderDetail(Long orderId) {

        Order order = lambdaQuery().eq(Order::getOrderId, orderId)
                .one();
        if(order == null){
            return Result.fail(404, "订单不存在");
        }
        Long userId = UserHolder.getUser().getUserId();
        if(!userId.equals(order.getUserId())){
            return Result.fail(403, "无权限查看此订单");
        }
        OrderDetailDTO orderDetailDTO = BeanUtil.copyProperties(order, OrderDetailDTO.class);
        Address address = addressService.lambdaQuery()
                .eq(Address::getAddressId, order.getAddressId())
                .one();
        List<OrderItem> orderItemlist = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, order.getOrderId())
                .list();//不会为null, 只会返回空列表

        OrderDetailDTO.AddressDTO addressDTO = new OrderDetailDTO.AddressDTO();
        if(address != null){
            BeanUtil.copyProperties(address, OrderDetailDTO.AddressDTO.class);
        }
        List<OrderDetailDTO.OrderItemDetailDTO> orderItemDTOlist = orderItemlist.stream()
                .map(orderItem -> BeanUtil.copyProperties(orderItem, OrderDetailDTO.OrderItemDetailDTO.class))
                .collect(Collectors.toList());
        orderDetailDTO.setAddress(addressDTO);
        orderDetailDTO.setItems(orderItemDTOlist);
        return Result.success(200, "获取成功", orderDetailDTO);
    }

    @Override
    public Result cancelOrder(Long orderId, String cancelReason) {
        Long userId = UserHolder.getUser().getUserId();
        //1.判断订单是否存在
        Order order = getById(orderId);
        if(order == null){
            return Result.fail(404, "订单不存在");
        }
        //2.用户是否有权限
        if(!userId.equals(order.getUserId())){
            return Result.fail(403, "无权操作此订单");
        }
        //3.状态校验
        String orderStatus = order.getOrderStatus();
        if(!"P".equals(orderStatus)){
            return Result.fail(400, "订单状态不允许取消");
        }
        //4.恢复库存
        //4.执行取消（恢复库存、返还优惠券、改状态）
        doCancel(order, cancelReason);

        return Result.success(200, "取消成功", null);
    }
    @Override
    public void autoCancelExpired(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return;
        }
        doCancel(order, "超时未支付，系统自动取消");
    }

    /** 取消订单的公共逻辑：CAS改状态 → 恢复库存 → 返还优惠券 */
    private void doCancel(Order order, String cancelReason) {
        transactionTemplate.executeWithoutResult(status -> {
            // 原来的 CAS 改状态 + for 循环恢复库存 + 返还优惠券

        // CAS：只有仍是 P(待付款) 才能改成 D，防止和支付回调并发打架
        boolean cancelled = lambdaUpdate()
                .eq(Order::getOrderId, order.getOrderId())
                .eq(Order::getOrderStatus, "P")
                .set(Order::getOrderStatus, "D")
                .set(Order::getCancelReason, cancelReason)
                .set(Order::getCloseTime, LocalDateTime.now())
                .update();
        if (!cancelled) {
            // 已支付或已取消，什么都不做（状态机幂等兜底）
            return;
        }

        // 1.恢复DB库存 + Redis库存
        List<OrderItem> list = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, order.getOrderId())
                .list();
        for (OrderItem orderItem : list) {
            productSkuService.lambdaUpdate()
                    .eq(ProductSku::getSkuId, orderItem.getSkuId())
                    .setSql("stock = stock + " + orderItem.getQuantity())
                    .update();
            stringRedisTemplate.opsForValue()
                    .increment("stock:sku:" + orderItem.getSkuId(), orderItem.getQuantity());
        }

        // 2.返还优惠券
        Long couponId = order.getCouponId();
        if (couponId != null) {
            couponService.lambdaUpdate()
                    .eq(Coupon::getCouponId, couponId)
                    .setSql("remain_count = remain_count + 1")
                    .update();
        }
        });
    }

    @Override
    public Result confirm(Long orderId) {
        //1.查询订单
        Order order = getById(orderId);
        if(order == null){
            return Result.fail(404, "订单id不存在");
        }
        //2.权限校验
        Long userId = UserHolder.getUser().getUserId();
        if(!order.getUserId().equals(userId)){
            return Result.fail(403, "无权限确认收获");
        }
        //3.状态校验
        //TODO接口文档这时候好像还没有设置orderStatus为S的接口
        //TODO所以这个接口方法我还没有用ApiFox检验
        if(!"S".equals(order.getOrderStatus())){
            return Result.fail(400, "订单状态不允许确认收货");
        }

        //4.更新状态
        lambdaUpdate().eq(Order::getOrderId, orderId)
                .set(Order::getOrderStatus, "R")
                .set(Order::getReceiveTime, LocalDateTime.now())
                .update();
        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)//传入一个orderId, 查询orderItem表里的字段orderId是实参的数据, 得到的集合就是
                .list();
        //新增, 累加销量排行榜
        for(OrderItem item : orderItems){
            //productId是Long类型, 要转变成String类型
            //quantity是Integer类型, 要转变成double类型
            redisTemplate.opsForZSet().incrementScore("rank:sales",
                    String.valueOf(item.getProductId()),
                    item.getQuantity().doubleValue()
                    );
        }
        return Result.success(200, "确认成功", null);
    }


    @Override
    public Result getLogistics(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }

        Long userId = UserHolder.getUser().getUserId();
        if (!order.getUserId().equals(userId)) {
            return Result.fail(403, "无权查看此订单物流");
        }

        LogisticsTracking tracking = logisticsTrackingService.lambdaQuery()
                .eq(LogisticsTracking::getOrderId, orderId)
                .one();
        if (tracking == null) {
            return Result.fail(404, "暂无物流信息");
        }

        LogisticsCompany company = logisticsCompanyService.getById(tracking.getLogisticsCompanyId());
        String companyName = company != null ? company.getCompanyName() : "";

        List<LogisticsDetail> detailList = logisticsDetailService.lambdaQuery()
                .eq(LogisticsDetail::getTrackingId, tracking.getTrackingId())
                .orderByDesc(LogisticsDetail::getStatusTime)
                .list();

        List<LogisticsReturnDTO.LogisticsDetailDTO> details = detailList.stream()
                .map(detail -> {
                    LogisticsReturnDTO.LogisticsDetailDTO dto = new LogisticsReturnDTO.LogisticsDetailDTO();
                    dto.setStatusTime(detail.getStatusTime() != null ? detail.getStatusTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                    dto.setStatusDescription(detail.getStatusDescription());
                    dto.setStatusLocation(detail.getStatusLocation());
                    return dto;
                })
                .collect(Collectors.toList());

        LogisticsReturnDTO logisticsReturnDTO = new LogisticsReturnDTO();
        logisticsReturnDTO.setTrackingId(tracking.getTrackingId());
        logisticsReturnDTO.setLogisticsCompany(companyName);
        logisticsReturnDTO.setTrackingNumber(tracking.getTrackingNumber());
        logisticsReturnDTO.setCurrentStatus(tracking.getCurrentStatus());
        logisticsReturnDTO.setCurrentLocation(tracking.getCurrentLocation());
        logisticsReturnDTO.setDetails(details);

        return Result.success(200, "获取成功", logisticsReturnDTO);
    }



    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
        int random = new Random().nextInt(900000) + 100000;
        return dateTime + random;
    }

}
