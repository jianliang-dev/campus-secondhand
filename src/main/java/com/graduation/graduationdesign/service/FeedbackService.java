package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Feedback;

import java.util.List;

/**
 * 留言反馈服务接口
 */
public interface FeedbackService {
    void addFeedback(Long userId, String type, String content);
    List<Feedback> getMyFeedback(Long userId);
}
