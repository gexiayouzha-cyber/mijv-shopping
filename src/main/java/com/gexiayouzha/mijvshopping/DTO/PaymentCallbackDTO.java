package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCallbackDTO {
    private String paymentNumber;
    private String transactionId;
    private String paymentStatus;
}