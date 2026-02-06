package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 系统公告实体类
 */
@Data
public class Notice {
    private Long id;
    private String title;     // 公告标题
    private String content;   // 公告内容
    private Date createTime;  // 发布时间
}
