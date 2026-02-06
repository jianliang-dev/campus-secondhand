package com.graduation.graduationdesign.entity;

import lombok.Data;

import java.util.Date;

/**
 * 社区分类实体类（圈子）
 */
@Data
public class CircleCategory {
    private Long id;
    private String name;        // 分类名称
    private Integer status;     // 状态：1-启用 0-禁用
    private Integer sortOrder;  // 排序
    private Date createTime;
    private Date updateTime;
}
