package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 求购实体类
 */
@Data
public class Wanted {
    private Long id;
    private Long userId;        // 发布用户ID
    private String title;       // 求购标题
    private Long categoryId;    // 类别ID
    private String expected;    // 期望（成色/型号等）
    private String description; // 求购描述
    private BigDecimal budget;  // 预算价格
    private Integer status;     // 状态：1-进行中 0-已完成
    private Date createTime;    // 发布时间
}
