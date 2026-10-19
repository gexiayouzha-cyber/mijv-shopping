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
@TableName("tb_logistics_company")
public class LogisticsCompany {
    @TableId(value = "id", type = IdType.AUTO)
    private Long companyId;

    private String companyCode;

    private String companyName;

    private String companyNameEn;

    private String website;

    private String hotline;

    private Integer isActive;

    private Integer sortOrder;

    private LocalDateTime createTime;
}