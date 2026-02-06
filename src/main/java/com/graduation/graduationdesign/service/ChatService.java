package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.vo.ChatMessageVO;
import com.graduation.graduationdesign.vo.ChatSessionVO;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {
    ChatMessageVO sendMessage(Long senderId, Long receiverId, String content);
    List<ChatMessageVO> getChatHistory(String sessionId, Long currentUserId);
    List<ChatSessionVO> getChatSessions(Long userId);
    List<ChatSessionVO> searchSessions(Long userId, String keyword);
    void markRead(String sessionId, Long userId);
    ChatSessionVO getOrCreateSession(Long userId, Long otherUserId);
}
