package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage {
    private Long id;
    private String sessionId;  // 会话ID
    private Long senderId;    // 发送者ID
    private Long receiverId;  // 接收者ID
    private String content;   // 消息内容
    private String type;      // 消息类型：text
    private Integer status;   // 状态：0-未读 1-已读
    private Date sendTime;    // 发送时间
}
