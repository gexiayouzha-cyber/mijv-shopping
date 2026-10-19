package com.gexiayouzha.mijvshopping.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long userId;

    private String userName;

    private String phoneNumber;

    private String passwordHash;

    @TableField(exist = false)
    private String password;

    private String email;

    private String nickname;

    @TableField("avatar_url")
    private String avatarURL;

    private String gender;

    private LocalDate birthday;

    private Integer userLevel;

    private Integer totalSpent;

    private Integer bonusPoints;


}
