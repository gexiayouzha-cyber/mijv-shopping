package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.*;
import com.gexiayouzha.mijvshopping.entity.User;
import com.gexiayouzha.mijvshopping.mapper.UserMapper;
import com.gexiayouzha.mijvshopping.service.IUserService;
import com.gexiayouzha.mijvshopping.utils.JwtUtils;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//如果不继承, 那么就要这个类自己实现它的接口的所有方法
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Result register(User user) {
        boolean exists = query().eq("user_name", user.getUserName()).exists();
        if (exists) {
            return Result.fail(600, "用户名重复");
        }

        boolean exists1 = query().eq("phone_number", user.getPhoneNumber()).exists();
        if (exists1) {
            return Result.fail(601, "手机号已被注册");
        }


        String password = user.getPassword();

        //单向加密, 只能比对, 不能解码
        String passwordHash = DigestUtil.bcrypt(password);
        user.setPasswordHash(passwordHash);
        save(user);
        UserSignDTO userSignDTO = BeanUtil.copyProperties(user, UserSignDTO.class);
        return Result.success(200, "注册成功", userSignDTO);
    }

    @Override
    public Result login(User user) {
        //传入了username,password(不是passwordhash)
        //新增
        String userName = user.getUserName();
        String failKey = "login:fail:"+userName;
        Long failCount = (Long)redisTemplate.opsForValue().get(failKey);
        if(failCount!=null && failCount>=5){
            return Result.fail(429, "尝试次数过多, 请10分钟后重试");
        }
        //2.我原有的登陆逻辑
        String password = user.getPassword();
        User userExist = query().eq("user_name", user.getUserName()).one();
        if (userExist == null) {
            //3.登陆失败, 计数+1
            Long newCount = redisTemplate.opsForValue().increment(failKey);
            if(newCount==1){//一分钟内登陆5次, 则休息10min
                redisTemplate.expire(failKey, 1, TimeUnit.MINUTES);
            }
            if(newCount!=null&&newCount>=5){
                redisTemplate.expire(failKey, 10, TimeUnit.MINUTES);
            }

            return Result.fail(603, "用户名或密码错误");
        }
        //获取密文
        String passwordHash = userExist.getPasswordHash();
        //检查明文和密文是否相等
        boolean isTrue = DigestUtil.bcryptCheck(password, passwordHash);

        if (!isTrue) {
            return Result.fail(603, "用户名或密码错误");
        }
        //造map, 再把map变成jwt, 自己想把的用户信息存进jwt
        String phoneNumber = userExist.getPhoneNumber();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userExist.getUserId());
        claims.put("userName", user.getUserName());
        String jwt = JwtUtils.generateJwt(claims);

        //传入Result里的data, data里要写token和user
        UserSignDTO userSignDTO = BeanUtil.copyProperties(userExist, UserSignDTO.class);
        Map<String, Object> dataMap = new LinkedHashMap<>();//hashmap不保证顺序,但好像顺序不是很重要
        dataMap.put("token", jwt);
        dataMap.put("user", userSignDTO);

        return Result.success(200, "登陆成功", dataMap);

    }

    @Override
    public Result getUserInfo() {
        UserInfoDTO userInfoDTO = UserHolder.getUser();
        return Result.success(200, "获取成功", userInfoDTO);
    }

    @Override
    public Result update(UserUpdateDTO userUpdateDTO) {
        //思路:先取出user, 更新, 再用updateById更新这个id的其他字段(参数是实体类的对象, 而不死id,
        // id是用来找到数据库里要修改哪一行的)
        Long userId = UserHolder.getUser().getUserId();
        User user = query().eq("id", userId).one();
        if(user == null) {
            return Result.fail(404,"用户不存在");
        }
        //只要前端传入的字段是非空的, 就修改相应的字段
        BeanUtil.copyProperties(userUpdateDTO, user, CopyOptions.create().setIgnoreNullValue(true));
        updateById(user);
        UserUpdateReturnDTO userUpdateReturnDTO = BeanUtil.copyProperties(user, UserUpdateReturnDTO.class);
        return Result.success(200,"更新成功", userUpdateReturnDTO);
    }

}
