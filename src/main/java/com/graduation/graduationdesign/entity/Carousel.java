package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 轮播图实体类
 */
@Data
public class Carousel {
    private Long id;
    private String imageUrl;   // 图片地址
    private String linkUrl;    // 跳转链接
    private Integer sortOrder; // 排序值
    private Integer status;    // 状态：1-启用 0-禁用
    private Date createTime;   // 创建时间
}
