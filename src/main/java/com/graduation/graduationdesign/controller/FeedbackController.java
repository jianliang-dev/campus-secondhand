package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Feedback;
import com.graduation.graduationdesign.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 留言反馈控制器
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交反馈（需要登录）
     */
    @PostMapping("/add")
    public Map<String, Object> addFeedback(@RequestParam String content,
                                            @RequestParam(required = false) String type,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            String safeType = (type == null || type.trim().isEmpty()) ? "其他" : type.trim();
            feedbackService.addFeedback(userId, safeType, content);
            result.put("code", 200);
            result.put("message", "反馈提交成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取我的反馈列表（需要登录）
     */
    @GetMapping("/my")
    public Map<String, Object> getMyFeedback(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<Feedback> feedbackList = feedbackService.getMyFeedback(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", feedbackList);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
