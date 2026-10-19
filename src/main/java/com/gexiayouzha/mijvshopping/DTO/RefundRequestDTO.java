package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestDTO {
    private Long orderId;
    private Long orderItemId;
    private String refundType;
    private String refundReason;
    private BigDecimal refundAmount;
}