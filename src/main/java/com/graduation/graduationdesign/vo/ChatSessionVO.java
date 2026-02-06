package com.graduation.graduationdesign.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话列表视图对象
 */
@Data
public class ChatSessionVO {
    private Long id;
    private String sessionId;
    private Long userId;
    private Long otherUserId;
    private String otherName;
    private String otherAvatar;
    private String lastMessage;
    private LocalDateTime lastTime;
    private Integer unreadCount;
}
