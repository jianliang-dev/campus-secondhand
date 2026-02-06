package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.vo.BuyerOrderListVO;
import com.graduation.graduationdesign.vo.SellerOrderListVO;

import java.util.List;

public interface OrderService {
    // 创建订单、查我的订单
    void createOrder(Long productId, String token);
    List<OrderInfo> myOrders(String token);

    // 回调接口需要的方法
    OrderInfo getByOrderNo(String orderNo);
    void paySuccess(String orderNo);

    // 模拟支付（本地演示用）
    void mockPay(Long orderId, String token);

    // 取消订单（买家）
    void cancelOrder(Long orderId, String token);

    //买家订单查询方法
    List<BuyerOrderListVO> listBuyerOrders(Long userId);
    //卖家订单查询方法
    List<SellerOrderListVO> listSellerOrders(Long userId);
}