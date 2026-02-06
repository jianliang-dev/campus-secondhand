package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.entity.Carousel;
import com.graduation.graduationdesign.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮播图控制器
 */
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    /**
     * 获取启用的轮播图列表（无需登录，前台用）
     */
    @GetMapping("/list")
    public Map<String, Object> getEnabledCarousels() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Carousel> carousels = carouselService.getEnabledCarousels();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", carousels);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
