package com.graduation.graduationdesign.service;


import com.graduation.graduationdesign.entity.User;

public interface UserService {
    User login(String username, String password);
    
    String register(String username, String password, String phone);
    
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    User getUserInfo(Long userId);

    User updateProfile(Long userId, String phone, String avatar, String signature);
}
