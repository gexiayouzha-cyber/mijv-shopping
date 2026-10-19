package com.gexiayouzha.mijvshopping.utils;

import com.gexiayouzha.mijvshopping.DTO.UserInfoDTO;

public class UserHolder {
    public static final ThreadLocal<UserInfoDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserInfoDTO user) {tl.set(user);}

    public static UserInfoDTO getUser() {return tl.get();}

    public static void removeUser() {tl.remove();}
}
