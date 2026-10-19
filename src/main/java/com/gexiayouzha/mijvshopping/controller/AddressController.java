package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.AddressReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Address;
import com.gexiayouzha.mijvshopping.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class AddressController {
    @Autowired
    private IAddressService addressServiceImpl;

    @PostMapping("/addresses")
    public Result addAddress(@RequestBody Address address) {
        return addressServiceImpl.addAddress(address);
    }

    @GetMapping("/addresses")
    public Result listAddresses() {
        return addressServiceImpl.listAddresses();
    }

    @PutMapping("/addresses/{addressId}")
    public Result updateAddress(@PathVariable Long addressId,
                                @RequestBody AddressReturnDTO address) {
        return addressServiceImpl.updateAddress(addressId, address);
    }

    @PutMapping("/addresses/{addressId}/default")
    public Result setDefaultAddress(@PathVariable Long addressId) {
        return addressServiceImpl.setDefaultAddress(addressId);
    }

    @DeleteMapping("/addresses/{addressId}")
    public Result deleteAddress(@PathVariable Long addressId) {
        return addressServiceImpl.delete(addressId);
    }

}
