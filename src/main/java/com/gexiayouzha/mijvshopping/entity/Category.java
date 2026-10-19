package com.gexiayouzha.mijvshopping.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_category")
public class Category {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer categoryId;
    private Integer parentId;
    private String categoryName;
    private Integer categoryLevel;
    private Integer sortOrder;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
