package com.graduation.graduationdesign.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.graduationdesign.service.ChatService;
import com.graduation.graduationdesign.vo.ChatMessageVO;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode root = objectMapper.readTree(message.getPayload());
            String type = root.path("type").asText();
            if (!"send".equals(type)) {
                return;
            }
            Long fromUserId = (Long) session.getAttributes().get("userId");
            Long toUserId = root.path("toUserId").asLong();
            String content = root.path("content").asText("");
            String clientId = root.path("clientId").asText("");
            if (fromUserId == null || toUserId == null || content.trim().isEmpty()) {
                return;
            }
            ChatMessageVO saved = chatService.sendMessage(fromUserId, toUserId, content.trim());
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "message");
            payload.put("data", saved);
            payload.put("clientId", clientId);

            sendToUser(fromUserId, payload);
            if (!fromUserId.equals(toUserId)) {
                sendToUser(toUserId, payload);
            }
        } catch (Exception ignored) {
            // 忽略解析或发送异常
        }
    }

    private void sendToUser(Long userId, Map<String, Object> payload) {
        WebSocketSession target = sessions.get(userId);
        if (target == null || !target.isOpen()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(payload);
            target.sendMessage(new TextMessage(json));
        } catch (Exception ignored) {
            // ignore
        }
    }
}
