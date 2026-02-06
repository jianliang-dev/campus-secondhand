package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 帖子实体类
 */
@Data
public class Post {
    private Long id;
    private Long userId;        // 发布用户ID
    private String circleName;  // 所属圈子
    private String title;       // 帖子标题
    private String content;     // 帖子内容
    private Integer status;     // 状态：1-正常 0-隐藏
    private Integer viewCount;  // 浏览量
    private Integer likeCount;  // 点赞数
    private Date lastReplyTime; // 最新回复时间
    private Date createTime;    // 发布时间
}
