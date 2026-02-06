package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Favorite;
import com.graduation.graduationdesign.mapper.FavoriteMapper;
import com.graduation.graduationdesign.vo.FavoriteProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务实现类（toggle：收藏/取消收藏切换）
 */
@Service
public class FavoriteServiceImpl {

    @Autowired
    private FavoriteMapper favoriteMapper;

    public void toggle(Long productId, String token) {
        // 1. 校验token有效性（必传且有效）
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }

        // 2. 解析用户ID
        Long userId = JwtUtil.getUserId(token);

        // 3. 切换收藏状态（防重复：已收藏则删除，未收藏则新增）
        if (favoriteMapper.exists(userId, productId) > 0) {
            favoriteMapper.delete(userId, productId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favoriteMapper.insert(favorite);
        }
    }

    // 新增：判断商品是否被当前用户收藏（供商品详情页使用）
    public boolean isFavorited(Long productId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            return false; // 未登录则默认未收藏
        }
        Long userId = JwtUtil.getUserId(token);
        return favoriteMapper.exists(userId, productId) > 0;
    }

    public List<FavoriteProductVO> listFavorites(String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        Long userId = JwtUtil.getUserId(token);
        return favoriteMapper.listByUserId(userId);
    }
}