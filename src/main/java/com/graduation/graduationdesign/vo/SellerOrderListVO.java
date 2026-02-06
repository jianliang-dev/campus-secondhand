package com.graduation.graduationdesign.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SellerOrderListVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalPrice;
    private Integer orderStatus;
    private Integer payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;

    // 商品信息（和买家VO一致）
    private Long productId;
    private String productTitle;
    private String coverImg;

    // 卖家订单显示买家信息
    private Long buyerId;
    private String buyerName;
    private String buyerAvatar;
}