package com.gexiayouzha.mijvshopping.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private String nickname;

    @TableField("avatar_url")
    private String avatarURL;

    private String gender;

    private String birthday;
}
