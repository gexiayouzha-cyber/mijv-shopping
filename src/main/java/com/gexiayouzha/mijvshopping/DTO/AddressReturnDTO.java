package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressReturnDTO {
    private Long addressId;

    private String receiverName;

    private Long phoneNumber;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String postalCode;

    private Integer isDefault;
}
