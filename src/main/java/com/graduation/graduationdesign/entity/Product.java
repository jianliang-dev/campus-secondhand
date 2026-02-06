package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类（与数据库字段对齐）
 */
@Data
public class Product {
    private Long id;             // 商品ID
    private Long userId;         // 发布者ID
    private String title;        // 商品标题
    private String description;  // 商品描述
    private BigDecimal price;    // 商品价格（对应数据库DECIMAL，避免精度丢失）
    private Long categoryId;     // 分类ID
    private String coverImg;     // 封面图
    private Integer status;      // 状态：1-上架 0-下架
    private Date createTime;     // 创建时间
    private Date updateTime;     // 更新时间
}