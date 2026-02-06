package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfo {
    private Long id;
    private String orderNo;       // 订单编号（UUID生成）
    private Long buyerId;         // 买家ID（当前登录用户）
    private Long sellerId;        // 卖家ID（商品发布者）
    private Long productId;       // 商品ID
    private BigDecimal totalPrice;// 订单总价（商品价格）
    private Integer orderStatus;  // 订单状态：0-待支付
    private Integer payStatus;    // 支付状态：0-未支付
    private Date payTime;         // 支付时间（暂为空）
    private Date createTime;      // 创建时间（数据库自动生成）
}