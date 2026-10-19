package com.gexiayouzha.mijvshopping.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateReturnDTO {
    private Long userId;

    private String nickname;

    @TableField("avatar_url")
    private String avatarURL;

    private String gender;

    private LocalDate birthday;
}
