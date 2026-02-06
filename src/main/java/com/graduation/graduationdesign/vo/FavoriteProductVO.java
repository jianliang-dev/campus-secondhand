package com.graduation.graduationdesign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏商品视图对象（包含商品与卖家信息）
 */
@Data
public class FavoriteProductVO {
    private Long favoriteId;
    private LocalDateTime favoriteTime;

    private Long productId;
    private String title;
    private BigDecimal price;
    private String coverImg;
    private Integer status;
    private LocalDateTime createTime;

    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;

    private Integer soldFlag;
}
