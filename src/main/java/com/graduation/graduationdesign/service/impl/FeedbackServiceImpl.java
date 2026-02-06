package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Feedback;
import com.graduation.graduationdesign.mapper.FeedbackMapper;
import com.graduation.graduationdesign.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 留言反馈服务实现类
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public void addFeedback(Long userId, String type, String content) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setType(type);
        feedback.setContent(content);
        feedbackMapper.insert(feedback);
    }

    @Override
    public List<Feedback> getMyFeedback(Long userId) {
        return feedbackMapper.findByUserId(userId);
    }
}
