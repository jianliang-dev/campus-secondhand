package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.PostLike;
import com.graduation.graduationdesign.mapper.PostLikeMapper;
import com.graduation.graduationdesign.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostMapper postMapper;

    public void toggle(Long postId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        Long userId = JwtUtil.getUserId(token);
        if (postLikeMapper.exists(userId, postId) > 0) {
            postLikeMapper.delete(userId, postId);
            postMapper.decrementLikeCount(postId);
        } else {
            PostLike like = new PostLike();
            like.setUserId(userId);
            like.setPostId(postId);
            postLikeMapper.insert(like);
            postMapper.incrementLikeCount(postId);
        }
    }

    public boolean isLiked(Long postId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            return false;
        }
        Long userId = JwtUtil.getUserId(token);
        return postLikeMapper.exists(userId, postId) > 0;
    }
}
