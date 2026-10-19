package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_logistics_tracking")
public class LogisticsTracking {
    @TableId(value = "id", type = IdType.AUTO)
    private Long trackingId;

    private Long orderId;

    private Integer logisticsCompanyId;

    private String trackingNumber;

    private String currentStatus;

    private String currentLocation;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}