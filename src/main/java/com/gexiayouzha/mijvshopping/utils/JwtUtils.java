package com.gexiayouzha.mijvshopping.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static String signKey="MijvShoppingSecretKey20260427abcdefghijklmnopqrstuvwxyz";
    private static Long expire = 43200000L;//毫秒, 对应12h

    //登陆接口会调用generateJwt
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }
    public static Claims parseJwt(String jwt) {
        //parse是解析的意思
        //claims存储的是用户信息集合, 是一个键值对Map
        Claims claims = Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt.substring("Bearer ".length()))
                .getBody();
        return claims;
    }
}
