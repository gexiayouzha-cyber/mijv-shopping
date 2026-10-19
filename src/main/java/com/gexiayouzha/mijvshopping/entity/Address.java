package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.function.ToDoubleBiFunction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user_address")
public class Address {
    //TODO:设置create_time自动创建
    @TableId(value = "id", type = IdType.AUTO)
    private Long addressId;

    private Long userId;

    private String receiverName;

    //用string, 因为可能0开头
    private String phoneNumber;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String postalCode;

    private Integer isDefault;

    @TableField(fill = FieldFill.INSERT)//插入数据(对象)时自动填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时自动填充
    private LocalDateTime updateTime;

}
