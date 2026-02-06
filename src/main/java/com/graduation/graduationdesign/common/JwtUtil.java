package com.graduation.graduationdesign.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JWT工具类
 */
public class JwtUtil {

    // 密钥
    private static final String SECRET_KEY = "graduation_secret";
    // 令牌过期时间：1天（单位：毫秒）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 天

    /**
     * 生成JWT令牌（根据用户ID）
     * @param userId 用户ID
     * @return 生成的令牌字符串
     */
    public static String generateToken(Long userId) {
        return Jwts.builder()
                // 设置主题：存储用户ID
                .setSubject(String.valueOf(userId))
                // 设置签发时间
                .setIssuedAt(new Date())
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // HS256算法 + 字符串密钥
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                // 拼接令牌
                .compact();
    }

    /**
     * 解析JWT令牌，获取用户ID
     * @param token 前端传入的令牌
     * @return 解析出的用户ID
     */
    public static Long getUserId(String token) {
        // 直接用parser()
        Claims claims = Jwts.parser()
                // 设置签名密钥
                .setSigningKey(SECRET_KEY)
                // 解析令牌获取Claims
                .parseClaimsJws(token)
                .getBody();

        // 从Claims中获取用户ID并转为Long
        return Long.valueOf(claims.getSubject());
    }
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}