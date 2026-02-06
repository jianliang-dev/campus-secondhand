package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 评论实体类
 */
@Data
public class Comment {
    private Long id;              // 评论ID
    private Long userId;          // 评论用户ID
    private Long targetId;        // 目标ID（商品/帖子/求购）
    private Integer targetType;   // 目标类型：1-商品 2-帖子 3-求购
    private String content;       // 评论内容
    private Date createTime;      // 评论时间
}
