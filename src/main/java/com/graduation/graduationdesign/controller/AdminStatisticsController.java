package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.AdminUtil;
import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.entity.Feedback;
import com.graduation.graduationdesign.entity.Product;
import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.mapper.FeedbackMapper;
import com.graduation.graduationdesign.mapper.OrderMapper;
import com.graduation.graduationdesign.mapper.ProductMapper;
import com.graduation.graduationdesign.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 管理员数据统计控制器
 */
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 管理后台概览数据（KPI + 待处理提醒）
     */
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);

            Map<String, Object> data = new HashMap<>();

            List<User> allUsers = userMapper.findAll();
            List<Product> allProducts = productMapper.findAllForAdmin();
            List<OrderInfo> allOrders = orderMapper.findAllForAdmin();
            List<Feedback> allFeedbacks = feedbackMapper.findAll(null, null, null);

            data.put("totalUsers", allUsers.size());
            long activeProducts = allProducts.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                    .count();
            data.put("totalProducts", allProducts.size());
            data.put("activeProducts", activeProducts);

            data.put("totalOrders", allOrders.size());

            LocalDate today = LocalDate.now();
            long todayOrders = allOrders.stream()
                    .filter(o -> o.getCreateTime() != null)
                    .filter(o -> LocalDate.ofInstant(o.getCreateTime().toInstant(), ZoneId.systemDefault())
                            .isEqual(today))
                    .count();
            data.put("todayOrders", todayOrders);

            BigDecimal todaySales = allOrders.stream()
                    .filter(o -> o.getPayStatus() != null && o.getPayStatus() == 1)
                    .filter(o -> o.getPayTime() != null)
                    .filter(o -> LocalDate.ofInstant(o.getPayTime().toInstant(), ZoneId.systemDefault())
                            .isEqual(today))
                    .map(OrderInfo::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.put("todaySales", todaySales);

            BigDecimal totalSales = allOrders.stream()
                    .filter(o -> o.getPayStatus() != null && o.getPayStatus() == 1)
                    .map(OrderInfo::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.put("totalSales", totalSales);

            long pendingOrders = allOrders.stream()
                    .filter(o -> o.getOrderStatus() != null && o.getOrderStatus() == 0)
                    .count();
            long completedOrders = allOrders.stream()
                    .filter(o -> o.getOrderStatus() != null && (o.getOrderStatus() == 1 || o.getOrderStatus() == 2))
                    .count();
            long canceledOrders = allOrders.stream()
                    .filter(o -> o.getOrderStatus() != null && o.getOrderStatus() == 3)
                    .count();
            Map<String, Long> orderStatusCounts = new LinkedHashMap<>();
            orderStatusCounts.put("pending", pendingOrders);
            orderStatusCounts.put("completed", completedOrders);
            orderStatusCounts.put("canceled", canceledOrders);
            data.put("orderStatusCounts", orderStatusCounts);

            long pendingCount = allFeedbacks.stream()
                    .filter(f -> f.getStatus() == null || f.getStatus() == 0)
                    .count();
            data.put("pendingFeedbackCount", pendingCount);

            List<Feedback> recentFeedbacks = allFeedbacks.stream()
                    .filter(f -> f.getStatus() == null || f.getStatus() == 0)
                    .limit(5)
                    .toList();
            data.put("recentFeedbacks", recentFeedbacks);

            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 近N天订单与销售额趋势
     */
    @GetMapping("/trend")
    public Map<String, Object> getTrend(@RequestParam(defaultValue = "7") Integer days,
                                        @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            int range = days == null || days < 1 ? 7 : days;

            List<OrderInfo> allOrders = orderMapper.findAllForAdmin();
            LocalDate start = LocalDate.now().minusDays(range - 1L);

            Map<String, Integer> orderCountMap = new LinkedHashMap<>();
            Map<String, BigDecimal> salesMap = new LinkedHashMap<>();
            for (int i = 0; i < range; i++) {
                LocalDate date = start.plusDays(i);
                String key = date.toString();
                orderCountMap.put(key, 0);
                salesMap.put(key, BigDecimal.ZERO);
            }

            for (OrderInfo order : allOrders) {
                if (order.getCreateTime() == null) {
                    continue;
                }
                LocalDate orderDate = LocalDate.ofInstant(order.getCreateTime().toInstant(), ZoneId.systemDefault());
                if (orderDate.isBefore(start)) {
                    continue;
                }
                String key = orderDate.toString();
                if (orderCountMap.containsKey(key)) {
                    orderCountMap.put(key, orderCountMap.get(key) + 1);
                    if (order.getPayStatus() != null && order.getPayStatus() == 1) {
                        BigDecimal current = salesMap.get(key);
                        salesMap.put(key, current.add(order.getTotalPrice()));
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("dates", orderCountMap.keySet());
            data.put("orderCounts", orderCountMap.values());
            data.put("salesAmounts", salesMap.values());

            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }
    /**
     * 获取统计数据概览（管理员）
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            
            Map<String, Object> data = new HashMap<>();
            
            // 用户统计
            List<User> allUsers = userMapper.findAll();
            data.put("totalUsers", allUsers.size());
            long activeUsers = allUsers.stream().filter(u -> u.getStatus() != null && u.getStatus() == 1).count();
            data.put("activeUsers", activeUsers);
            
            // 商品统计
            List<Product> allProducts = productMapper.findAllForAdmin();
            data.put("totalProducts", allProducts.size());
            long activeProducts = allProducts.stream().filter(p -> p.getStatus() != null && p.getStatus() == 1).count();
            data.put("activeProducts", activeProducts);
            
            // 订单统计
            List<OrderInfo> allOrders = orderMapper.findAllForAdmin();
            data.put("totalOrders", allOrders.size());
            long paidOrders = allOrders.stream().filter(o -> o.getPayStatus() != null && o.getPayStatus() == 1).count();
            data.put("paidOrders", paidOrders);
            
            // 销售额统计
            BigDecimal totalSales = allOrders.stream()
                    .filter(o -> o.getPayStatus() != null && o.getPayStatus() == 1)
                    .map(OrderInfo::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.put("totalSales", totalSales);
            
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
