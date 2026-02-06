package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论视图对象（包含用户信息）
 */
@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;      // 评论者用户名
    private String avatar;        // 评论者头像
    private Long targetId;
    private Integer targetType;
    private String content;
    private LocalDateTime createTime;
}
