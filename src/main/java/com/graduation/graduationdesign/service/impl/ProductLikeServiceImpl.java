package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.ProductLike;
import com.graduation.graduationdesign.mapper.ProductLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 点赞服务实现类（toggle：点赞/取消点赞切换）
 */
@Service
public class ProductLikeServiceImpl {

    @Autowired
    private ProductLikeMapper productLikeMapper;

    public void toggle(Long productId, String token) {
        // 1. 校验token有效性（必传且有效）
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }

        // 2. 解析用户ID
        Long userId = JwtUtil.getUserId(token);

        // 3. 切换点赞状态（防重复：已点赞则删除，未点赞则新增）
        if (productLikeMapper.exists(userId, productId) > 0) {
            productLikeMapper.delete(userId, productId);
        } else {
            ProductLike like = new ProductLike();
            like.setUserId(userId);
            like.setProductId(productId);
            productLikeMapper.insert(like);
        }
    }

    // 新增：判断商品是否被当前用户点赞（供商品详情页使用）
    public boolean isLiked(Long productId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            return false; // 未登录则默认未点赞
        }
        Long userId = JwtUtil.getUserId(token);
        return productLikeMapper.exists(userId, productId) > 0;
    }
}