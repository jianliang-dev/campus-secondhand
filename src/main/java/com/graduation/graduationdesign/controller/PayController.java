package com.graduation.graduationdesign.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alibaba.fastjson.JSONObject;
import com.graduation.graduationdesign.config.AlipayConfig;
import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝沙箱支付控制器
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 生成支付表单（用户点击“去支付”调用）
     * 访问：http://localhost:8080/pay/alipay?orderId=1
     */
    @GetMapping(value = "/alipay", produces = "text/html; charset=UTF-8")
    public String alipay(@RequestParam Long orderId) throws AlipayApiException {
        // 1. 根据订单ID查询订单（校验订单存在）
        OrderInfo order = orderMapper.findById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        // 2. 校验订单是否待支付（避免重复支付）
        if (order.getOrderStatus() != 0) {
            return "该订单已支付，无需重复支付";
        }

        // 3. 创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.GATEWAY_URL,
                AlipayConfig.APP_ID,
                AlipayConfig.APP_PRIVATE_KEY,
                "json",
                AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,
                AlipayConfig.SIGN_TYPE
        );

        // 4. 构建支付请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // 同步回调（支付完成后跳转的页面，给用户看）
        request.setReturnUrl("http://localhost:8080/pay/success");
        // 异步通知（支付宝主动通知后端更新订单状态）
        request.setNotifyUrl("http://localhost:8080/pay/notify");

        // 5. 组装支付参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOrderNo()); // 订单号
        bizContent.put("total_amount", order.getTotalPrice()); // 支付金额
        bizContent.put("subject", "二手商品订单支付"); // 支付标题
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY"); // 固定值
        request.setBizContent(bizContent.toString());

        // 6. 生成支付表单（POST方式，避免URL过长导致打不开）
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
        return response.getBody();

    }

    /**
     * 同步回调（支付完成后跳转，给用户看）
     */
    @GetMapping("/success")
    public String success() {
        return "<h1>支付成功！</h1><p>请返回订单列表查看状态</p>";
    }

    /**
     * 异步通知（支付宝主动调用，更新订单状态）
     * 注意：必须是POST请求，且支付宝会多次调用（以确保更新成功）
     */
    @PostMapping("/notify")
    public String alipayNotify(
            @RequestParam("out_trade_no") String outTradeNo,
            @RequestParam("trade_status") String tradeStatus) {

        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            orderMapper.updatePaySuccess(outTradeNo);
        }

        return "success";
    }

}