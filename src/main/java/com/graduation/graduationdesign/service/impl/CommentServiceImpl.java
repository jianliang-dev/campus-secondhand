package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Comment;
import com.graduation.graduationdesign.mapper.CommentMapper;
import com.graduation.graduationdesign.mapper.PostMapper;
import com.graduation.graduationdesign.service.CommentService;
import com.graduation.graduationdesign.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public void addComment(Long userId, Long targetId, Integer targetType, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetId(targetId);
        comment.setTargetType(targetType);
        comment.setContent(content);
        commentMapper.insert(comment);
        if (targetType != null && targetType == 2) {
            postMapper.updateLastReplyTime(targetId);
        }
    }

    @Override
    public List<CommentVO> getComments(Long targetId, Integer targetType) {
        return commentMapper.findByTarget(targetId, targetType);
    }

    @Override
    public List<Comment> getMyComments(Long userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该评论");
        }
        commentMapper.delete(commentId);
    }
}
