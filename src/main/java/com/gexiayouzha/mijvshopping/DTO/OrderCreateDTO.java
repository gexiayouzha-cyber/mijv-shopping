package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {
    private Long addressId;
    private List<Long> cartIds;
    private List<OrderItemDTO> items;
    private Long couponId;
    private Integer useBonusPoints;
    private String orderRemarks;
}
