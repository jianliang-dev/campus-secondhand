package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Address;
import com.graduation.graduationdesign.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 添加地址（需要登录）
     */
    @PostMapping("/add")
    public Map<String, Object> addAddress(@RequestBody Address address,
                                          @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            addressService.addAddress(address, userId);
            result.put("code", 200);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取地址列表（需要登录）
     */
    @GetMapping("/list")
    public Map<String, Object> getAddresses(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<Address> addresses = addressService.getAddresses(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", addresses);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取地址详情（需要登录）
     */
    @GetMapping("/{id}")
    public Map<String, Object> getAddress(@PathVariable Long id,
                                          @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            Address address = addressService.getAddress(id, userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", address);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 更新地址（需要登录）
     */
    @PostMapping("/update")
    public Map<String, Object> updateAddress(@RequestBody Address address,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            addressService.updateAddress(address, userId);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 删除地址（需要登录）
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteAddress(@RequestParam Long id,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            addressService.deleteAddress(id, userId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 设置默认地址（需要登录）
     */
    @PostMapping("/setDefault")
    public Map<String, Object> setDefaultAddress(@RequestParam Long id,
                                                 @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            addressService.setDefaultAddress(id, userId);
            result.put("code", 200);
            result.put("message", "设置成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
