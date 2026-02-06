package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.service.impl.ProductLikeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞接口
 */
@RestController
@RequestMapping("/like")
public class ProductLikeController {

    @Autowired
    private ProductLikeServiceImpl likeService;

    /**
     * 点赞/取消点赞切换
     */
    @PostMapping("/toggle")
    public String toggle(@RequestParam Long productId,
                         @RequestHeader("Authorization") String token) {
        try {
            likeService.toggle(productId, token);
            return "操作成功";
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回错误提示（如“未登录或token无效”）
        }
    }

    /**
     * 判断商品是否被当前用户点赞（供商品详情页使用）
     */
    @GetMapping("/check")
    public boolean check(@RequestParam Long productId,
                         @RequestHeader(value = "Authorization", required = false) String token) {
        return likeService.isLiked(productId, token);
    }
}