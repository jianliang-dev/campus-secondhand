package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.service.impl.OrderServiceImpl;
import com.graduation.graduationdesign.vo.BuyerOrderListVO;
import com.graduation.graduationdesign.vo.SellerOrderListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * 创建订单接口（需要登录）
     */
    @PostMapping("/create")
    public String create(@RequestParam Long productId,
                         @RequestHeader("Authorization") String token) {
        try {
            orderService.createOrder(productId, token);
            return "下单成功，等待支付";
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回错误提示（如“商品不存在”“未登录”）
        }
    }

    /**
     * 我的订单接口（需要登录）
     */
    @GetMapping("/my")
    public Object myOrders(@RequestHeader("Authorization") String token) {
        try {
            List<OrderInfo> orderList = orderService.myOrders(token);
            return orderList; // 返回我的订单列表
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回错误提示
        }
    }
    //买家订单查询
    @GetMapping("/buyer/list")
    public List<BuyerOrderListVO> listBuyerOrders(@RequestHeader("Authorization") String token) {
        // 从token解析当前登录用户ID（buyer_id）
        Long userId = JwtUtil.getUserId(token);
        return orderService.listBuyerOrders(userId);
    }
    //卖家订单查询
    @GetMapping("/seller/list")
    public List<SellerOrderListVO> listSellerOrders(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserId(token);
        return orderService.listSellerOrders(userId);
    }

    /**
     * 模拟支付（本地演示用）
     */
    @PostMapping("/mockPay")
    public Map<String, Object> mockPay(@RequestParam Long orderId,
                                       @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.mockPay(orderId, token);
            result.put("code", 200);
            result.put("message", "支付成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 取消订单（买家）
     */
    @PostMapping("/cancel")
    public Map<String, Object> cancelOrder(@RequestParam Long orderId,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.cancelOrder(orderId, token);
            result.put("code", 200);
            result.put("message", "取消成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}