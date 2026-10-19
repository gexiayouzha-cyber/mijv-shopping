package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.RefundRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.service.IRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/refunds")
public class RefundController {

    @Autowired
    private IRefundService refundService;

    @PostMapping
    public Result applyRefund(@RequestBody RefundRequestDTO refundRequestDTO) {
        return refundService.applyRefund(refundRequestDTO);
    }
}