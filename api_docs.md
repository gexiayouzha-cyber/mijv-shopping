# 电商平台接口库文档

## 1. 接口设计原则

1. **RESTful API 设计风格**：使用 HTTP 方法（GET、POST、PUT、DELETE）表示操作类型
2. **统一响应格式**：所有接口返回统一的 JSON 格式，包含 code、message、data 字段
3. **安全性**：使用 JWT 进行身份认证，敏感接口需要权限验证
4. **版本控制**：API 路径中包含版本号，如 /api/v1/users
5. **错误处理**：统一的错误码体系，详细的错误信息

## 2. 接口分类

### 2.1 用户模块
- 注册、登录、登出
- 个人信息管理
- 地址管理
- 收藏管理
- 优惠券管理

### 2.2 商品模块
- 商品列表
- 商品详情
- 商品分类
- 商品搜索
- 商品评价

### 2.3 购物车模块
- 添加商品到购物车
- 修改购物车商品数量
- 删除购物车商品
- 获取购物车列表

### 2.4 订单模块
- 创建订单
- 订单列表
- 订单详情
- 订单状态更新
- 物流查询

### 2.5 支付模块
- 支付请求
- 支付回调
- 退款申请

### 2.6 系统模块
- 日志管理
- 数据统计

## 3. 详细接口定义

### 3.1 用户模块

#### 3.1.1 注册接口
- **路径**：`/api/v1/users/register`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | userName | string | 是 | 用户名 |
  | password | string | 是 | 密码 |
  | email | string | 是 | 邮箱 |
  | phoneNumber | string | 否 | 手机号 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "userId": 1,
      "userName": "test",
      "email": "test@example.com"
    }
  }
  ```

#### 3.1.2 登录接口
- **路径**：`/api/v1/users/login`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | userName | string | 是 | 用户名 |
  | password | string | 是 | 密码 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "jwt_token",
      "user": {
        "userId": 1,
        "userName": "test",
        "email": "test@example.com"
      }
    }
  }
  ```

#### 3.1.3 获取用户信息
- **路径**：`/api/v1/users/info`
- **方法**：GET
- **参数**：无（需要 JWT 认证）
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "userId": 1,
      "userName": "test",
      "email": "test@example.com",
      "nickname": "测试用户",
      "avatarURL": "https://example.com/avatar.jpg",
      "gender": "M",
      "birthday": "1990-01-01",
      "userLevel": 1,
      "totalSpent": 0,
      "bonusPoints": 0
    }
  }
  ```

#### 3.1.4 更新用户信息
- **路径**：`/api/v1/users/info`
- **方法**：PUT
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | nickname | string | 否 | 昵称 |
  | avatarURL | string | 否 | 头像URL |
  | gender | string | 否 | 性别（M/F/U） |
  | birthday | string | 否 | 生日 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "userId": 1,
      "nickname": "新昵称",
      "avatarURL": "https://example.com/new_avatar.jpg",
      "gender": "M",
      "birthday": "1990-01-01"
    }
  }
  ```

#### 3.1.5 地址管理
- **路径**：`/api/v1/users/addresses`
- **方法**：GET
- **参数**：无（需要 JWT 认证）
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": [
      {
        "addressId": 1,
        "receiverName": "张三",
        "phoneNumber": "13800138000",
        "province": "广东省",
        "city": "深圳市",
        "district": "南山区",
        "detailAddress": "科技园路1号",
        "postalCode": "518000",
        "isDefault": 1
      }
    ]
  }
  ```

- **添加地址**：POST /api/v1/users/addresses
- **更新地址**：PUT /api/v1/users/addresses/{addressId}
- **删除地址**：DELETE /api/v1/users/addresses/{addressId}
- **设置默认地址**：PUT /api/v1/users/addresses/{addressId}/default

### 3.2 商品模块

#### 3.2.1 商品列表
- **路径**：`/api/v1/products`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | categoryId | int | 否 | 分类ID |
  | page | int | 否 | 页码，默认1 |
  | pageSize | int | 否 | 每页数量，默认10 |
  | sort | string | 否 | 排序方式（salesCount/price/createTime） |
  | order | string | 否 | 排序顺序（asc/desc） |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "list": [
        {
          "productId": 1,
          "productName": "商品名称",
          "price": 99.99,
          "originalPrice": 199.99,
          "mainImageURL": "https://example.com/image.jpg",
          "salesCount": 100,
          "stock": 50
        }
      ],
      "total": 100,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.2.2 商品详情
- **路径**：`/api/v1/products/{productId}`
- **方法**：GET
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "productId": 1,
      "productName": "商品名称",
      "price": 99.99,
      "originalPrice": 199.99,
      "stock": 50,
      "mainImageURL": "https://example.com/image.jpg",
      "imageURLs": ["https://example.com/image1.jpg", "https://example.com/image2.jpg"],
      "description": "商品描述",
      "productDetail": "商品详情",
      "salesCount": 100,
      "viewCount": 500,
      "favoriteCount": 50,
      "rating": 4.8,
      "ratingCount": 20,
      "isFreeShipping": 1,
      "shippingFee": 0,
      "attributes": [
        {
          "attributeName": "颜色",
          "attributeValue": "红色"
        }
      ],
      "skus": [
        {
          "skuId": 1,
          "skuName": "红色",
          "price": 99.99,
          "stock": 20,
          "skuImageURL": "https://example.com/sku1.jpg",
          "skuAttributes": "颜色:红色"
        }
      ]
    }
  }
  ```

#### 3.2.3 商品分类
- **路径**：`/api/v1/categories`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | parentId | int | 否 | 父分类ID，默认NULL（顶级分类） |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": [
      {
        "categoryId": 1,
        "categoryName": "电子产品",
        "categoryLevel": 1,
        "parentId": 0,
        "sortOrder": 1,
        "isActive": 1
      }
    ]
  }
  ```

#### 3.2.4 商品搜索
- **路径**：`/api/v1/products/search`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | keyword | string | 是 | 搜索关键词 |
  | page | int | 否 | 页码，默认1 |
  | pageSize | int | 否 | 每页数量，默认10 |
- **返回值**：与商品列表相同

#### 3.2.5 商品评价
- **路径**：`/api/v1/products/{productId}/reviews`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | page | int | 否 | 页码，默认1 |
  | pageSize | int | 否 | 每页数量，默认10 |
  | rating | int | 否 | 评分筛选（1-5） |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "list": [
        {
          "reviewId": 1,
          "userId": 1,
          "userName": "test",
          "rating": 5,
          "reviewContent": "商品很好",
          "reviewImages": ["https://example.com/review1.jpg"],
          "createTime": "2023-01-01 12:00:00",
          "sellerReply": "感谢您的评价"
        }
      ],
      "total": 50,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

### 3.3 购物车模块

#### 3.3.1 添加商品到购物车
- **路径**：`/api/v1/cart`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | productId | bigint | 是 | 商品ID |
  | skuId | bigint | 否 | SKU ID |
  | quantity | int | 是 | 数量 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "添加成功",
    "data": {
      "cartId": 1,
      "productId": 1,
      "skuId": 1,
      "quantity": 2,
      "productName": "商品名称",
      "skuName": "红色",
      "price": 99.99,
      "imageURL": "https://example.com/image.jpg"
    }
  }
  ```

#### 3.3.2 获取购物车列表
- **路径**：`/api/v1/cart`
- **方法**：GET
- **参数**：无（需要 JWT 认证）
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": [
      {
        "cartId": 1,
        "productId": 1,
        "skuId": 1,
        "quantity": 2,
        "isSelected": 1,
        "productName": "商品名称",
        "skuName": "红色",
        "price": 99.99,
        "imageURL": "https://example.com/image.jpg",
        "stock": 50
      }
    ],
    "totalAmount": 199.98
  }
  ```

#### 3.3.3 更新购物车商品
- **路径**：`/api/v1/cart/{cartId}`
- **方法**：PUT
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | quantity | int | 否 | 数量 |
  | isSelected | int | 否 | 是否选中（0/1） |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": {
      "cartId": 1,
      "quantity": 3,
      "isSelected": 1
    }
  }
  ```

#### 3.3.4 删除购物车商品
- **路径**：`/api/v1/cart/{cartId}`
- **方法**：DELETE
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 3.4 订单模块

#### 3.4.1 创建订单
- **路径**：`/api/v1/orders`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | addressId | bigint | 是 | 地址ID |
  | cartIds | array | 否 | 购物车商品ID列表 |
  | items | array | 否 | 商品列表（与cartIds二选一） |
  | couponId | bigint | 否 | 优惠券ID |
  | useBonusPoints | int | 否 | 使用积分数量 |
  | orderRemarks | string | 否 | 订单备注 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "orderId": 1,
      "orderNumber": "2023010100000001",
      "totalAmount": 199.98,
      "discountAmount": 20,
      "actualPaidAmount": 179.98,
      "paymentDeadline": "2023-01-01 12:30:00"
    }
  }
  ```

#### 3.4.2 获取订单列表
- **路径**：`/api/v1/orders`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | status | string | 否 | 订单状态 |
  | page | int | 否 | 页码，默认1 |
  | pageSize | int | 否 | 每页数量，默认10 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "list": [
        {
          "orderId": 1,
          "orderNumber": "2023010100000001",
          "orderStatus": "P",
          "totalAmount": 199.98,
          "actualPaidAmount": 179.98,
          "createTime": "2023-01-01 12:00:00",
          "paymentDeadline": "2023-01-01 12:30:00"
        }
      ],
      "total": 10,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.4.3 获取订单详情
- **路径**：`/api/v1/orders/{orderId}`
- **方法**：GET
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "orderId": 1,
      "orderNumber": "2023010100000001",
      "orderStatus": "P",
      "totalAmount": 199.98,
      "discountAmount": 20,
      "shippingFee": 0,
      "actualPaidAmount": 179.98,
      "paymentMethod": null,
      "paymentTime": null,
      "shippingMethod": null,
      "trackingNumber": null,
      "shippingTime": null,
      "receiveTime": null,
      "createTime": "2023-01-01 12:00:00",
      "address": {
        "receiverName": "张三",
        "phoneNumber": "13800138000",
        "province": "广东省",
        "city": "深圳市",
        "district": "南山区",
        "detailAddress": "科技园路1号"
      },
      "items": [
        {
          "orderItemId": 1,
          "productId": 1,
          "skuId": 1,
          "productName": "商品名称",
          "skuName": "红色",
          "productImageURL": "https://example.com/image.jpg",
          "unitPrice": 99.99,
          "quantity": 2,
          "totalAmount": 199.98
        }
      ]
    }
  }
  ```

#### 3.4.4 取消订单
- **路径**：`/api/v1/orders/{orderId}/cancel`
- **方法**：PUT
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | cancelReason | string | 是 | 取消原因 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "取消成功"
  }
  ```

#### 3.4.5 确认收货
- **路径**：`/api/v1/orders/{orderId}/confirm`
- **方法**：PUT
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "确认成功"
  }
  ```

#### 3.4.6 物流查询
- **路径**：`/api/v1/orders/{orderId}/logistics`
- **方法**：GET
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "trackingId": 1,
      "logisticsCompany": "顺丰速运",
      "trackingNumber": "SF1234567890",
      "currentStatus": "运输中",
      "currentLocation": "深圳市",
      "details": [
        {
          "statusTime": "2023-01-01 10:00:00",
          "statusDescription": "快递已发出",
          "statusLocation": "广州市"
        },
        {
          "statusTime": "2023-01-01 12:00:00",
          "statusDescription": "快递已到达",
          "statusLocation": "深圳市"
        }
      ]
    }
  }
  ```

### 3.5 支付模块

#### 3.5.1 支付请求
- **路径**：`/api/v1/payments`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | orderId | bigint | 是 | 订单ID |
  | paymentMethod | string | 是 | 支付方式（A-支付宝, W-微信, B-银行转账, C-信用卡, P- PayPal） |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "请求成功",
    "data": {
      "paymentId": 1,
      "paymentNumber": "P2023010100000001",
      "paymentUrl": "https://example.com/pay?order=123",
      "qrCode": "base64编码的二维码"
    }
  }
  ```

#### 3.5.2 支付回调
- **路径**：`/api/v1/payments/callback`
- **方法**：POST
- **参数**：由支付平台回调时提供
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "回调成功"
  }
  ```

#### 3.5.3 退款申请
- **路径**：`/api/v1/refunds`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | orderId | bigint | 是 | 订单ID |
  | orderItemId | bigint | 否 | 订单商品ID（部分退款时必填） |
  | refundType | string | 是 | 退款类型（R-仅退款, E-退货退款） |
  | refundReason | string | 是 | 退款原因 |
  | refundAmount | decimal | 是 | 退款金额 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "申请成功",
    "data": {
      "refundId": 1,
      "refundNumber": "R2023010100000001",
      "refundStatus": "P"
    }
  }
  ```

### 3.6 优惠券模块

#### 3.6.1 获取可用优惠券
- **路径**：`/api/v1/coupons/available`
- **方法**：GET
- **参数**：无（需要 JWT 认证）
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": [
      {
        "couponId": 1,
        "couponName": "满100减20",
        "couponType": "D",
        "discountValue": 20,
        "minOrderAmount": 100,
        "expireTime": "2023-12-31 23:59:59"
      }
    ]
  }
  ```

#### 3.6.2 领取优惠券
- **路径**：`/api/v1/coupons/{couponId}/receive`
- **方法**：POST
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "领取成功"
  }
  ```

### 3.7 收藏模块

#### 3.7.1 收藏商品
- **路径**：`/api/v1/favorites`
- **方法**：POST
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | productId | bigint | 是 | 商品ID |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "收藏成功"
  }
  ```

#### 3.7.2 获取收藏列表
- **路径**：`/api/v1/favorites`
- **方法**：GET
- **参数**：
  | 字段名 | 类型 | 必填 | 描述 |
  |-------|------|------|------|
  | page | int | 否 | 页码，默认1 |
  | pageSize | int | 否 | 每页数量，默认10 |
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "获取成功",
    "data": {
      "list": [
        {
          "favoriteId": 1,
          "productId": 1,
          "productName": "商品名称",
          "price": 99.99,
          "mainImageURL": "https://example.com/image.jpg",
          "createTime": "2023-01-01 12:00:00"
        }
      ],
      "total": 10,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.7.3 取消收藏
- **路径**：`/api/v1/favorites/{productId}`
- **方法**：DELETE
- **参数**：无
- **返回值**：
  ```json
  {
    "code": 200,
    "message": "取消成功"
  }
  ```

## 4. 接口调用示例

### 4.1 登录示例

```javascript
// 使用 fetch API 调用登录接口
fetch('/api/v1/users/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userName: 'test',
    password: '123456'
  })
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    // 登录成功，保存 token
    localStorage.setItem('token', data.data.token);
    console.log('登录成功');
  } else {
    console.error('登录失败:', data.message);
  }
})
.catch(error => {
  console.error('请求错误:', error);
});
```

### 4.2 获取商品列表示例

```javascript
// 使用 fetch API 调用商品列表接口
fetch('/api/v1/products?categoryId=1&page=1&pageSize=10', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    console.log('商品列表:', data.data.list);
  } else {
    console.error('获取失败:', data.message);
  }
})
.catch(error => {
  console.error('请求错误:', error);
});
```

### 4.3 创建订单示例

```javascript
// 使用 fetch API 调用创建订单接口
fetch('/api/v1/orders', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  },
  body: JSON.stringify({
    addressId: 1,
    cartIds: [1, 2],
    couponId: 1,
    useBonusPoints: 100,
    orderRemarks: '尽快发货'
  })
})
.then(response => response.json())
.then(data => {
  if (data.code === 200) {
    console.log('订单创建成功:', data.data);
  } else {
    console.error('创建失败:', data.message);
  }
})
.catch(error => {
  console.error('请求错误:', error);
});
```

## 5. 错误码定义

| 错误码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 501 | 接口未实现 |
| 600 | 用户名已存在 |
| 601 | 邮箱已存在 |
| 602 | 手机号已存在 |
| 603 | 用户名或密码错误 |
| 604 | 验证码错误 |
| 605 | 商品库存不足 |
| 606 | 订单已过期 |
| 607 | 支付失败 |
| 608 | 优惠券已过期 |
| 609 | 优惠券已使用 |
| 610 | 优惠券领取次数限制 |

## 6. 注意事项

1. **认证**：所有需要用户身份的接口都需要在请求头中添加 `Authorization: Bearer {token}`
2. **参数验证**：客户端需要对所有输入参数进行验证，确保数据格式正确
3. **错误处理**：客户端需要根据返回的错误码进行相应的处理
4. **性能优化**：对于大数据量的接口，建议使用分页和缓存
5. **安全性**：不要在前端存储敏感信息，如密码等

## 7. 接口版本管理

接口版本通过 URL 路径中的版本号进行管理，如 `/api/v1/users`。当接口发生不兼容的变更时，会增加版本号，如 `/api/v2/users`。

## 8. 总结

本接口库文档基于数据库结构设计，覆盖了电商平台的核心功能，包括用户管理、商品管理、购物车、订单、支付、优惠券等模块。接口设计遵循 RESTful 风格，提供了统一的响应格式和错误处理机制，方便前端开发调用。

在实际开发中，还需要根据具体的业务需求和技术栈进行适当的调整和扩展。