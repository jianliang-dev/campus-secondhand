package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.entity.Product;
import com.graduation.graduationdesign.mapper.OrderMapper;
import com.graduation.graduationdesign.mapper.ProductMapper;
import com.graduation.graduationdesign.service.OrderService;
import com.graduation.graduationdesign.vo.BuyerOrderListVO;
import com.graduation.graduationdesign.vo.SellerOrderListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// 新增：实现OrderService接口
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 创建订单（核心逻辑）
     */
    @Override // 新增：接口实现注解
    public void createOrder(Long productId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        Long buyerId = JwtUtil.getUserId(token);
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架，无法下单");
        }
        OrderInfo order = new OrderInfo();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setProductId(productId);
        order.setTotalPrice(product.getPrice());
        orderMapper.insert(order);
    }

    /**
     * 查询我的订单（原有方法，适配接口）
     */
    @Override // 新增：接口实现注解
    public List<OrderInfo> myOrders(String token) {
        // 原有代码不变
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        Long buyerId = JwtUtil.getUserId(token);
        return orderMapper.findByBuyer(buyerId);
    }

    /**
     * 根据订单号查询订单（支付回调用）
     */
    @Override // 新增：接口实现注解
    public OrderInfo getByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    /**
     * 支付成功更新订单状态（0→1）
     */
    @Override // 新增：接口实现注解
    @Transactional
    public void paySuccess(String orderNo) {
        // 原有代码不变
        OrderInfo order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return;
        }
        orderMapper.updatePaySuccess(orderNo);
    }

    // ===================== 新增：买家订单列表（联表查询） =====================
    @Override
    public List<BuyerOrderListVO> listBuyerOrders(Long userId) {
        // 直接调用Mapper的联表查询方法
        return orderMapper.listBuyerOrders(userId);
    }

    // ===================== 新增：卖家订单列表（联表查询） =====================
    @Override
    public List<SellerOrderListVO> listSellerOrders(Long userId) {
        // 直接调用Mapper的联表查询方法
        return orderMapper.listSellerOrders(userId);
    }

    /**
     * 模拟支付（本地演示用）
     */
    @Override
    @Transactional
    public void mockPay(Long orderId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Long userId = JwtUtil.getUserId(token);
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付");
        }
        orderMapper.updatePaySuccess(order.getOrderNo());
    }

    /**
     * 取消订单（买家）
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId, String token) {
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录或token无效");
        }
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        Long userId = JwtUtil.getUserId(token);
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            throw new RuntimeException("已支付订单无法取消");
        }
        if (order.getOrderStatus() != null && order.getOrderStatus() != 0) {
            throw new RuntimeException("当前订单不可取消");
        }
        orderMapper.updateOrderStatus(orderId, 3);
    }
}