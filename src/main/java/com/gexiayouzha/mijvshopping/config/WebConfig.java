package com.gexiayouzha.mijvshopping.config;

import com.gexiayouzha.mijvshopping.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration//这个注解导致了白名单会在后端接收到请求的第一时间判断, 如果在白名单里面, 那么不执行prehandler, 而直接进入controller
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/v1/users/register",
                        "/v1/users/login",
                        "/v1/products/{productId}",
                        "/v1/categories",
                        "/v1/products/{productId}/reviews",
                        "/v1/products/search",
                        "/v1/payments/callback"

                );
    }

    //配置返回的JSON字符串为编码UTF-8
  /*  @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.addAddress(0, stringConverter);
    }*/
}
