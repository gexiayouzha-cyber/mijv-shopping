package com.gexiayouzha.mijvshopping.controller;

import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.DTO.UserUpdateDTO;
import com.gexiayouzha.mijvshopping.entity.User;
import com.gexiayouzha.mijvshopping.service.IUserService;
import com.gexiayouzha.mijvshopping.utils.JwtUtils;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/login")
    //传入username, password
    public Result login(@RequestBody User user){ return userService.login(user);}

    @GetMapping("/info")
    public Result getUserInfo(){
        //HttpServletRequest request
        /*try {
            String jwt = request.getHeader("Authorization");
            Claims claims = JwtUtils.parseJwt(jwt);
            Long userId = claims.get("userId", Long.class);
            return userService.getUserInfo(userId);
        } catch (Exception e) {
            return Result()
        }*/
        return userService.getUserInfo();
    }

    @PutMapping("/info")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO){
        return userService.update(userUpdateDTO);
    }
}
