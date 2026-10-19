package com.gexiayouzha.mijvshopping.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderDetailDTO {
        private Long orderId;
        private String orderNumber;
        private String orderStatus;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal shippingFee;
        private BigDecimal actualPaidAmount;
        private String paymentMethod;
        private LocalDateTime paymentTime;
        private String shippingMethod;
        private String trackingNumber;
        private LocalDateTime shippingTime;
        private LocalDateTime receiveTime;
        private LocalDateTime createTime;
        private AddressDTO address;
        private List<OrderItemDetailDTO> items;

        // 内部类：地址信息
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AddressDTO {
            private String receiverName;
            private String phoneNumber;
            private String province;
            private String city;
            private String district;
            private String detailAddress;
        }

        // 内部类：订单项信息
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OrderItemDetailDTO {
            private Long orderItemId;
            private Long productId;
            private Long skuId;
            private String productName;
            private String skuName;
            private String productImageURL;
            private BigDecimal unitPrice;
            private Integer quantity;
            private BigDecimal totalAmount;
        }
    }

