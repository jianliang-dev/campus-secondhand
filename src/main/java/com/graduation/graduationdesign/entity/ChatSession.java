package com.graduation.graduationdesign.entity;

import lombok.Data;

import java.util.Date;

/**
 * 聊天会话实体（用户维度）
 */
@Data
public class ChatSession {
    private Long id;
    private String sessionId;   // 会话ID（同一对用户共享）
    private Long userId;        // 当前用户
    private Long otherUserId;   // 对方用户
    private String lastMessage; // 最后一条消息
    private Date lastTime;      // 最后消息时间
    private Integer unreadCount;// 未读数
    private Date createTime;
    private Date updateTime;
}
