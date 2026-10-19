package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.PaymentCallbackDTO;
import com.gexiayouzha.mijvshopping.DTO.PaymentRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Payment;

public interface IPaymentService extends IService<Payment> {
    Result createPayment(PaymentRequestDTO paymentRequestDTO);
    Result paymentCallback(PaymentCallbackDTO callbackDTO);
}