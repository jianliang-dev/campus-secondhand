package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SearchHistory {
    private Long id;          // 对应数据库id
    private Long userId;      // 对应数据库user_id（未登录则为null）
    private String keyword;   // 对应数据库keyword
    private Date createTime;  // 对应数据库create_time
}