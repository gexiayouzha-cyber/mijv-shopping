package com.gexiayouzha.mijvshopping.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.AddressReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Address;

public interface IAddressService extends IService<Address> {
    Result addAddress(Address address);

    Result updateAddress(Long addressId, AddressReturnDTO address);

    Result delete(Long addressId);

    Result listAddresses();

    Result setDefaultAddress(Long addressId);
}
