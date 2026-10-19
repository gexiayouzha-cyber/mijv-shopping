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
@TableName("tb_logistics_detail")
public class LogisticsDetail {
    @TableId(value = "id", type = IdType.AUTO)
    private Long logisticsId;

    private Long trackingId;

    private LocalDateTime statusTime;

    private String statusDescription;

    private String statusLocation;

    private LocalDateTime createTime;
}