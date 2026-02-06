package com.graduation.graduationdesign.common;

import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.mapper.UserMapper;

/**
 * 管理员权限验证工具类
 * 注意：需要在Controller中注入UserMapper后调用
 */
public class AdminUtil {

    /**
     * 验证是否为管理员
     * @param token 用户token
     * @param userMapper UserMapper实例
     * @return true-是管理员，false-不是管理员
     */
    public static boolean isAdmin(String token, UserMapper userMapper) {
        try {
            if (!JwtUtil.validateToken(token)) {
                return false;
            }
            Long userId = JwtUtil.getUserId(token);
            User user = userMapper.findById(userId);
            return user != null && user.getRole() != null && user.getRole() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证并获取管理员用户ID
     * @param token 用户token
     * @param userMapper UserMapper实例
     * @return 管理员用户ID，如果不是管理员则抛出异常
     */
    public static Long getAdminId(String token, UserMapper userMapper) {
        if (!JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录");
        }
        Long userId = JwtUtil.getUserId(token);
        User user = userMapper.findById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            throw new RuntimeException("无管理员权限");
        }
        return userId;
    }
}
