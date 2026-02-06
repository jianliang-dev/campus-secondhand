package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子视图对象（包含用户信息）
 */
@Data
public class PostVO {
    private Long id;
    private Long userId;
    private String username;      // 发布者用户名
    private String avatar;        // 发布者头像
    private String circleName;
    private String title;
    private String content;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer hotScore;
    private LocalDateTime lastReplyTime;
    private LocalDateTime createTime;
}
