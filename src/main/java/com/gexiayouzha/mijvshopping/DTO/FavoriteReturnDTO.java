package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteReturnDTO {
    private List<FavoriteItemDTO> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FavoriteItemDTO {
        private Long favoriteId;
        private Long productId;
        private String productName;
        private BigDecimal price;
        private String mainImageURL;
        private String createTime;
    }
}
