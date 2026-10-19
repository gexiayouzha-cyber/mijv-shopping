package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReturnDTO {
    private Long paymentId;
    private String paymentNumber;
    private String paymentUrl;
    private String qrCode;
}