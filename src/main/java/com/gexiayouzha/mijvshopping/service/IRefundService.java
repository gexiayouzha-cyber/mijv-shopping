package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.RefundRequestDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Refund;

public interface IRefundService extends IService<Refund> {
    Result applyRefund(RefundRequestDTO refundRequestDTO);
}