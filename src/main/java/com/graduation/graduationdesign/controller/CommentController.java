package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Comment;
import com.graduation.graduationdesign.service.CommentService;
import com.graduation.graduationdesign.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论（需要登录）
     * targetType: 1-商品 2-帖子 3-求购
     */
    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestParam Long targetId,
                                          @RequestParam Integer targetType,
                                          @RequestParam String content,
                                          @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            commentService.addComment(userId, targetId, targetType, content);
            result.put("code", 200);
            result.put("message", "评论成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取评论列表（无需登录）
     * targetType: 1-商品 2-帖子 3-求购
     */
    @GetMapping("/list")
    public Map<String, Object> getComments(@RequestParam Long targetId,
                                           @RequestParam Integer targetType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<CommentVO> comments = commentService.getComments(targetId, targetType);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", comments);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取我的评论列表（需要登录）
     */
    @GetMapping("/my")
    public Map<String, Object> getMyComments(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<Comment> comments = commentService.getMyComments(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", comments);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 删除评论（需要登录）
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteComment(@RequestParam Long id,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            commentService.deleteComment(id, userId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
