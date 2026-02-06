package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.ChatMessage;
import com.graduation.graduationdesign.entity.ChatSession;
import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.mapper.ChatMessageMapper;
import com.graduation.graduationdesign.mapper.ChatSessionMapper;
import com.graduation.graduationdesign.mapper.UserMapper;
import com.graduation.graduationdesign.service.ChatService;
import com.graduation.graduationdesign.vo.ChatMessageVO;
import com.graduation.graduationdesign.vo.ChatSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ChatMessageVO sendMessage(Long senderId, Long receiverId, String content) {
        String sessionId = buildSessionId(senderId, receiverId);

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType("text");
        message.setStatus(0);
        message.setSendTime(new java.util.Date());
        chatMessageMapper.insert(message);

        // 更新/创建会话（发送方）
        ChatSession senderSession = chatSessionMapper.findByUserAndOther(senderId, receiverId);
        if (senderSession == null) {
            senderSession = new ChatSession();
            senderSession.setSessionId(sessionId);
            senderSession.setUserId(senderId);
            senderSession.setOtherUserId(receiverId);
            senderSession.setLastMessage(content);
            senderSession.setLastTime(new java.util.Date());
            senderSession.setUnreadCount(0);
            chatSessionMapper.insert(senderSession);
        } else {
            senderSession.setLastMessage(content);
            senderSession.setLastTime(new java.util.Date());
            senderSession.setUnreadCount(0);
            chatSessionMapper.updateSession(senderSession);
        }

        // 更新/创建会话（接收方）
        ChatSession receiverSession = chatSessionMapper.findByUserAndOther(receiverId, senderId);
        if (receiverSession == null) {
            receiverSession = new ChatSession();
            receiverSession.setSessionId(sessionId);
            receiverSession.setUserId(receiverId);
            receiverSession.setOtherUserId(senderId);
            receiverSession.setLastMessage(content);
            receiverSession.setLastTime(new java.util.Date());
            receiverSession.setUnreadCount(1);
            chatSessionMapper.insert(receiverSession);
        } else {
            receiverSession.setLastMessage(content);
            receiverSession.setLastTime(new java.util.Date());
            Integer unread = receiverSession.getUnreadCount() == null ? 0 : receiverSession.getUnreadCount();
            receiverSession.setUnreadCount(unread + 1);
            chatSessionMapper.updateSession(receiverSession);
        }

        return toMessageVO(message, senderId, receiverId);
    }

    @Override
    public List<ChatMessageVO> getChatHistory(String sessionId, Long currentUserId) {
        ChatSession session = chatSessionMapper.findBySessionIdAndUser(sessionId, currentUserId);
        if (session == null) {
            throw new RuntimeException("无权限查看该聊天记录");
        }
        return chatMessageMapper.getChatHistory(sessionId);
    }

    @Override
    public List<ChatSessionVO> getChatSessions(Long userId) {
        return chatSessionMapper.listSessions(userId);
    }

    @Override
    public List<ChatSessionVO> searchSessions(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return chatSessionMapper.listSessions(userId);
        }
        return chatSessionMapper.searchSessions(userId, keyword.trim());
    }

    @Override
    @Transactional
    public void markRead(String sessionId, Long userId) {
        ChatSession session = chatSessionMapper.findBySessionIdAndUser(sessionId, userId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        chatMessageMapper.markRead(sessionId, userId);
        chatSessionMapper.clearUnread(sessionId, userId);
    }

    @Override
    @Transactional
    public ChatSessionVO getOrCreateSession(Long userId, Long otherUserId) {
        ChatSession session = chatSessionMapper.findByUserAndOther(userId, otherUserId);
        String sessionId = buildSessionId(userId, otherUserId);
        if (session == null) {
            session = new ChatSession();
            session.setSessionId(sessionId);
            session.setUserId(userId);
            session.setOtherUserId(otherUserId);
            session.setLastMessage("");
            session.setLastTime(new java.util.Date());
            session.setUnreadCount(0);
            chatSessionMapper.insert(session);
        }
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setSessionId(session.getSessionId());
        vo.setUserId(session.getUserId());
        vo.setOtherUserId(session.getOtherUserId());
        vo.setLastMessage(session.getLastMessage());
        if (session.getLastTime() != null) {
            vo.setLastTime(java.time.LocalDateTime.ofInstant(session.getLastTime().toInstant(), java.time.ZoneId.systemDefault()));
        }
        vo.setUnreadCount(Objects.requireNonNullElse(session.getUnreadCount(), 0));
        User other = userMapper.findById(otherUserId);
        if (other != null) {
            vo.setOtherName(other.getUsername());
            vo.setOtherAvatar(other.getAvatar());
        }
        return vo;
    }

    private String buildSessionId(Long userId1, Long userId2) {
        long min = Math.min(userId1, userId2);
        long max = Math.max(userId1, userId2);
        return min + "_" + max;
    }

    private ChatMessageVO toMessageVO(ChatMessage message, Long senderId, Long receiverId) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setSessionId(message.getSessionId());
        vo.setSenderId(senderId);
        vo.setReceiverId(receiverId);
        vo.setContent(message.getContent());
        vo.setType(message.getType());
        vo.setStatus(message.getStatus());
        if (message.getSendTime() != null) {
            vo.setSendTime(java.time.LocalDateTime.ofInstant(message.getSendTime().toInstant(), java.time.ZoneId.systemDefault()));
        }
        User sender = userMapper.findById(senderId);
        User receiver = userMapper.findById(receiverId);
        if (sender != null) {
            vo.setSenderName(sender.getUsername());
            vo.setSenderAvatar(sender.getAvatar());
        }
        if (receiver != null) {
            vo.setReceiverName(receiver.getUsername());
            vo.setReceiverAvatar(receiver.getAvatar());
        }
        return vo;
    }
}
