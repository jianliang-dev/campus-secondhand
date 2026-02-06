package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.service.impl.FavoriteServiceImpl;
import com.graduation.graduationdesign.vo.FavoriteProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏接口
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteServiceImpl favoriteService;

    /**
     * 收藏/取消收藏切换
     */
    @PostMapping("/toggle")
    public String toggle(@RequestParam Long productId,
                         @RequestHeader("Authorization") String token) {
        try {
            favoriteService.toggle(productId, token);
            return "操作成功";
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回错误提示（如“未登录或token无效”）
        }
    }

    /**
     * 判断商品是否被当前用户收藏（供商品详情页使用）
     */
    @GetMapping("/check")
    public boolean check(@RequestParam Long productId,
                         @RequestHeader(value = "Authorization", required = false) String token) {
        return favoriteService.isFavorited(productId, token);
    }

    /**
     * 收藏列表（需要登录）
     */
    @GetMapping("/list")
    public Map<String, Object> list(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<FavoriteProductVO> list = favoriteService.listFavorites(token);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", list);
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}