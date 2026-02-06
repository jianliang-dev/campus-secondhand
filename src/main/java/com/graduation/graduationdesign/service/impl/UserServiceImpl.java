package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.mapper.UserMapper;
import com.graduation.graduationdesign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 标记为Spring的业务层组件，让Spring管理
@Service
// 实现UserService接口
public class UserServiceImpl implements UserService {

    // 注入UserMapper
    @Autowired
    private UserMapper userMapper;

    // 实现接口的login方法
    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.findByUsername(username);

        // 2. 校验：用户不存在 → 返回null
        if (user == null) {
            return null;
        }

        // 3. 校验：密码不匹配 → 返回null
        if (!user.getPassword().equals(password)) {
            return null;
        }

        // 4. 登录成功 → 返回用户信息
        return user;
    }

    // 用户注册
    @Override
    public String register(String username, String password, String phone) {
        // 1. 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(0); // 普通用户
        user.setStatus(1); // 正常状态

        // 3. 插入数据库
        userMapper.insert(user);
        return "注册成功";
    }

    // 修改密码
    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }

        // 3. 更新密码
        userMapper.updatePassword(userId, newPassword);
    }

    // 获取用户信息
    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 清除密码信息，不返回给前端
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateProfile(Long userId, String phone, String avatar, String signature) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateProfile(userId, phone, avatar, signature);
        User updated = userMapper.findById(userId);
        if (updated != null) {
            updated.setPassword(null);
        }
        return updated;
    }
}