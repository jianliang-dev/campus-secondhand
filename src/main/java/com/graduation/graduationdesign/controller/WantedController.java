package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Wanted;
import com.graduation.graduationdesign.service.WantedService;
import com.graduation.graduationdesign.vo.WantedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 求购控制器
 */
@RestController
@RequestMapping("/wanted")
public class WantedController {

    @Autowired
    private WantedService wantedService;

    /**
     * 发布求购信息（需要登录）
     */
    @PostMapping("/publish")
    public Map<String, Object> publishWanted(@RequestBody Wanted wanted,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            wantedService.publishWanted(wanted, userId);
            result.put("code", 200);
            result.put("message", "发布成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有求购列表（无需登录）
     */
    @GetMapping("/list")
    public Map<String, Object> getAllWanted(@RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(defaultValue = "latest") String sort) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<WantedVO> wantedList = wantedService.getAllWanted(categoryId, status, sort);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", wantedList);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取求购详情（无需登录）
     */
    @GetMapping("/{id}")
    public Map<String, Object> getWantedDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            WantedVO wanted = wantedService.getWantedDetail(id);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", wanted);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取我的求购列表（需要登录）
     */
    @GetMapping("/my")
    public Map<String, Object> getMyWanted(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<Wanted> wantedList = wantedService.getMyWanted(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", wantedList);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 更新求购信息（需要登录）
     */
    @PostMapping("/update")
    public Map<String, Object> updateWanted(@RequestBody Wanted wanted,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            wantedService.updateWanted(wanted, userId);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 删除求购信息（需要登录）
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteWanted(@RequestParam Long id,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            wantedService.deleteWanted(id, userId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 更新求购状态（关闭/开启，需要登录）
     */
    @PostMapping("/updateStatus")
    public Map<String, Object> updateWantedStatus(@RequestParam Long id,
                                                   @RequestParam Integer status,
                                                   @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            wantedService.updateWantedStatus(id, status, userId);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
