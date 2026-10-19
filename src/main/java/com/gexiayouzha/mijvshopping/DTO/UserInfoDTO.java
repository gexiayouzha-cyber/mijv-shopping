package com.gexiayouzha.mijvshopping.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private Long userId;

    private String userName;

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
