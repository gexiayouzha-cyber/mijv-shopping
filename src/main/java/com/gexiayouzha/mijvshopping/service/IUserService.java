package com.gexiayouzha.mijvshopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.DTO.UserUpdateDTO;
import com.gexiayouzha.mijvshopping.entity.User;

public interface IUserService extends IService<User> {
    Result register(User user);

    Result login(User user);

    Result getUserInfo();

    Result update(UserUpdateDTO userUpdateDTO);
}
