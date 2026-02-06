package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息视图对象（包含用户信息）
 */
@Data
public class ChatMessageVO {
    private Long id;
    private String sessionId;
    private Long senderId;
    private String senderName;    // 发送者用户名
    private String senderAvatar;  // 发送者头像
    private Long receiverId;
    private String receiverName;  // 接收者用户名
    private String receiverAvatar; // 接收者头像
    private String content;
    private String type;
    private Integer status;
    private LocalDateTime sendTime;
}
