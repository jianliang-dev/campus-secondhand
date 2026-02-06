package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.entity.CircleCategory;
import com.graduation.graduationdesign.service.CircleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区分类控制器（前台用）
 */
@RestController
@RequestMapping("/circleCategory")
public class CircleCategoryController {

    @Autowired
    private CircleCategoryService circleCategoryService;

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<CircleCategory> categories = circleCategoryService.getEnabledCategories();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", categories);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
