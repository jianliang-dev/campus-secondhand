package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Comment;
import com.graduation.graduationdesign.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {
    void addComment(Long userId, Long targetId, Integer targetType, String content);
    List<CommentVO> getComments(Long targetId, Integer targetType);
    List<Comment> getMyComments(Long userId);
    void deleteComment(Long commentId, Long userId);
}
