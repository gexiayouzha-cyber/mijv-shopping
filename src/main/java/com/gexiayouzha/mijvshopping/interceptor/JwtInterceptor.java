package com.gexiayouzha.mijvshopping.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.DTO.UserInfoDTO;
import com.gexiayouzha.mijvshopping.entity.User;
import com.gexiayouzha.mijvshopping.service.IUserService;
import com.gexiayouzha.mijvshopping.service.impl.UserServiceImpl;
import com.gexiayouzha.mijvshopping.utils.JwtUtils;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    private final UserServiceImpl userServiceImpl;

    public JwtInterceptor(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        return;
    }

    //response:返回给前端的
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //日志结果如果是error, 说明后端没接收到发起请求的路径, 可能后端接收路径语法有问题
        //可能前端url有问题
        log.info("拦截的路径: " + request.getRequestURI());
        //客户端输入过来的jwt
        String jwt = request.getHeader("Authorization");
        if("test-token-123".equals(jwt)){
            //这个相当于我不用登陆后generateJwt了, 直接自己造一个Jwt, 对比一下是否一样
            //而正常的逻辑, 传入的jwt是要解析的, "test-token-123".equals(jwt)相当于解析了
            UserInfoDTO testUser = new UserInfoDTO();
            testUser.setUserId(1L);
            testUser.setUserName("test-user");
            UserHolder.saveUser(testUser);
            return true;
        }
        if(StrUtil.isBlank(jwt)) {
            log.info("未授权,请先登录");
            withError(response, 401, "请先登陆");//由于这里response是HttpServletResponse的对象, 所以可以返回给前端报错信息, 而不是用return的方式
            return false;
        }
        //校验
        try{
            JwtUtils.parseJwt(jwt);//当令牌有效期过期时,令牌解析失败
        }
        catch(Exception e){
            e.printStackTrace();
            log.info("解析令牌失败, 返回未登录错误信息");
            withError(response, 401, "令牌解析失败");
            return false;
        }
        log.info("令牌合法, 放行");

        Claims claims = JwtUtils.parseJwt(jwt);
        //放到userHolder
        //UserHolder的userId是根据token里存的用户id获取的
        Long userId = claims.get("userId", Long.class);
        User user = userService.getById(userId);
        UserInfoDTO userInfoDTO = BeanUtil.copyProperties(user, UserInfoDTO.class);
        UserHolder.saveUser(userInfoDTO);
        return true;

    }

    //使得JSON用UTF-8编码
    private static void withError(HttpServletResponse response, int code, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");//告诉浏览器用JSON和UTF-8编码
        response.setContentType("application/json; charset=utf-8");
        Result<Void> error = Result.fail(code, message);
        response.getWriter().write(JSONUtil.toJsonStr(error));//这个才是真正返回给前端的内容
        //类似于{
        //  code: 401,
        //  message: "请先登录"
        //}
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
