package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) String phone) {
        Map<String, Object> result = new HashMap<>();
        try {
            String message = userService.register(username, password, phone);
            result.put("code", 200);
            result.put("message", message);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 用户登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.login(username, password);
        if (user == null) {
            result.put("code", 400);
            result.put("message", "用户名或密码错误");
        } else {
            String token = JwtUtil.generateToken(user.getId());
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", user);
        }
        return result;
    }

    // 获取用户信息（需要登录）
    @GetMapping("/profile")
    public Map<String, Object> profile(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 校验token是否有效
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "token 无效或已过期");
                return result;
            }
            // 2. 解析token获取用户ID
            Long userId = JwtUtil.getUserId(token);
            // 3. 获取用户完整信息
            User user = userService.getUserInfo(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 修改密码（需要登录）
    @PostMapping("/updatePassword")
    public Map<String, Object> updatePassword(@RequestParam String oldPassword,
                                              @RequestParam String newPassword,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 校验token
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "token 无效或已过期");
                return result;
            }
            // 2. 获取用户ID
            Long userId = JwtUtil.getUserId(token);
            // 3. 修改密码
            userService.updatePassword(userId, oldPassword, newPassword);
            result.put("code", 200);
            result.put("message", "密码修改成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 更新个人信息（需要登录）
    @PostMapping("/updateProfile")
    public Map<String, Object> updateProfile(@RequestBody User payload,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "token 无效或已过期");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            User updated = userService.updateProfile(userId, payload.getPhone(), payload.getAvatar(), payload.getSignature());
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updated);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 上传头像（需要登录）
    @PostMapping(value = "/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "token 无效或已过期");
                return result;
            }
            if (file == null || file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "请选择文件");
                return result;
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("code", 400);
                result.put("message", "仅支持图片文件");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            if (ext.isEmpty()) {
                ext = ".jpg";
            }
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "avatars");
            Files.createDirectories(uploadDir);
            String filename = userId + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
            Path targetPath = uploadDir.resolve(filename);
            file.transferTo(targetPath.toFile());
            String url = "/uploads/avatars/" + filename;
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败：" + e.getMessage());
        }
        return result;
    }
}