package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReturnDTO {
    private Long orderId;
    private String orderNumber;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal actualPaidAmount;
    private LocalDateTime createTime;
    private LocalDateTime paymentDeadline;
}
