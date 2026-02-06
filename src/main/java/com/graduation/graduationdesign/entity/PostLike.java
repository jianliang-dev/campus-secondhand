package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PostLike {
    private Long id;
    private Long userId;
    private Long postId;
    private Date createTime;
}
