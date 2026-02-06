package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BuyerOrderListVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalPrice;
    private Integer orderStatus;
    private Integer payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;

    // 商品信息
    private Long productId;
    private String productTitle;
    private String coverImg;

    // 卖家信息
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
}