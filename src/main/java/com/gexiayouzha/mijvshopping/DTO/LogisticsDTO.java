package com.gexiayouzha.mijvshopping.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsDTO {
    private Long trackingId;            // 物流ID
    private Long orderItemId;           // 新增：订单项ID
    private String productName;         // 新增：商品名称
    private String productImageURL;     // 新增：商品图片
    private String logisticsCompany;    // 物流公司
    private String trackingNumber;      // 物流单号
    private String currentStatus;       // 当前状态
    private String currentLocation;     // 当前位置
    private List<LogisticsDetailDTO> details;

    public static class LogisticsDetailDTO {
        private LocalDateTime statusTime;
        private String statusDescription;
        private String statusLocation;
    }
}
