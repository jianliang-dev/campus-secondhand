package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 求购视图对象（包含用户信息）
 */
@Data
public class WantedVO {
    private Long id;
    private Long userId;
    private String username;      // 发布者用户名
    private String avatar;        // 发布者头像
    private String title;
    private Long categoryId;
    private String categoryName;
    private String expected;
    private String description;
    private BigDecimal budget;
    private Integer status;
    private LocalDateTime createTime;
}
