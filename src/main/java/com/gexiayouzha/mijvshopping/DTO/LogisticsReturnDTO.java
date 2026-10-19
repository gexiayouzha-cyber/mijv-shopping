package com.gexiayouzha.mijvshopping.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsReturnDTO {
    private Long trackingId;
    private String logisticsCompany;
    private String trackingNumber;
    private String currentStatus;
    private String currentLocation;
    private List<LogisticsDetailDTO> details;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogisticsDetailDTO {
        private String statusTime;
        private String statusDescription;
        private String statusLocation;
    }
}