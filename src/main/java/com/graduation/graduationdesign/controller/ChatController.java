package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.service.ChatService;
import com.graduation.graduationdesign.vo.ChatMessageVO;
import com.graduation.graduationdesign.vo.ChatSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息（需要登录）
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestParam Long receiverId,
                                            @RequestParam String content,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long senderId = JwtUtil.getUserId(token);
            ChatMessageVO message = chatService.sendMessage(senderId, receiverId, content);
            result.put("code", 200);
            result.put("message", "发送成功");
            result.put("data", message);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取聊天记录（需要登录）
     */
    @GetMapping("/history")
    public Map<String, Object> getChatHistory(@RequestParam String sessionId,
                                               @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long currentUserId = JwtUtil.getUserId(token);
            List<ChatMessageVO> messages = chatService.getChatHistory(sessionId, currentUserId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", messages);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取聊天列表（需要登录）
     */
    @GetMapping("/sessions")
    public Map<String, Object> getChatSessions(@RequestParam(required = false) String keyword,
                                               @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            List<ChatSessionVO> chatList = chatService.searchSessions(userId, keyword);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", chatList);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 清空未读（需要登录）
     */
    @PostMapping("/read")
    public Map<String, Object> readSession(@RequestParam String sessionId,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            chatService.markRead(sessionId, userId);
            result.put("code", 200);
            result.put("message", "已读");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取或创建会话（需要登录）
     */
    @PostMapping("/session")
    public Map<String, Object> getOrCreateSession(@RequestParam Long otherUserId,
                                                  @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            ChatSessionVO session = chatService.getOrCreateSession(userId, otherUserId);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", session);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
