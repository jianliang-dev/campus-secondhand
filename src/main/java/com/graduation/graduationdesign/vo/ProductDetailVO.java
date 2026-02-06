package com.graduation.graduationdesign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品详情视图对象（含卖家信息）
 */
@Data
public class ProductDetailVO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String coverImg;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
}
