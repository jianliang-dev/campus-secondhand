package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Post;
import com.graduation.graduationdesign.service.PostService;
import com.graduation.graduationdesign.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 发布帖子（需要登录）
     */
    @PostMapping("/publish")
    public Map<String, Object> publishPost(@RequestBody Post post,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            postService.publishPost(post, userId);
            result.put("code", 200);
            result.put("message", "发布成功");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有帖子列表（无需登录）
     */
    @GetMapping("/list")
    public Map<String, Object> getAllPosts(@RequestParam(required = false) String circleName,
                                           @RequestParam(defaultValue = "latest") String sort,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PostVO> posts = postService.getPosts(circleName, sort, page, size);
            Integer total = postService.countPosts(circleName);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", posts);
            result.put("total", total);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 热门帖子（近7天）
     */
    @GetMapping("/hot")
    public Map<String, Object> getHotPosts(@RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PostVO> posts = postService.getHotPosts(limit);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", posts);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 根据圈子获取帖子列表（无需登录）
     */
    @GetMapping("/circle/{circleName}")
    public Map<String, Object> getPostsByCircle(@PathVariable String circleName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<PostVO> posts = postService.getPosts(circleName, "latest", 1, 10);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", posts);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取帖子详情（无需登录）
     */
    @GetMapping("/{id}")
    public Map<String, Object> getPostDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            postService.incrementViewCount(id);
            PostVO post = postService.getPostDetail(id);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", post);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取我的帖子列表（需要登录）
     */
    @GetMapping("/my")
    public Map<String, Object> getMyPosts(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<Post> posts = postService.getMyPosts(userId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", posts);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 更新帖子（需要登录）
     */
    @PostMapping("/update")
    public Map<String, Object> updatePost(@RequestBody Post post,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            postService.updatePost(post, userId);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 删除帖子（需要登录）
     */
    @PostMapping("/delete")
    public Map<String, Object> deletePost(@RequestParam Long id,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            postService.deletePost(id, userId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
