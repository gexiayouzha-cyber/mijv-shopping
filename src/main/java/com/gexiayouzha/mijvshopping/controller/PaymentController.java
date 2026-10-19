package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.PaymentCallbackDTO;
import com.gexiayouzha.mijvshopping.DTO.PaymentRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping
    public Result createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.createPayment(paymentRequestDTO);
    }

    @PostMapping("/callback")
    public Result paymentCallback(@RequestBody PaymentCallbackDTO callbackDTO) {
        return paymentService.paymentCallback(callbackDTO);
    }
}