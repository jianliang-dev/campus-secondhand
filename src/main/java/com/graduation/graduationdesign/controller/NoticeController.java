package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.entity.Notice;
import com.graduation.graduationdesign.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取所有公告列表（无需登录）
     */
    @GetMapping("/list")
    public Map<String, Object> getAllNotices() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notice> notices = noticeService.getAllNotices();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", notices);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取最新公告（无需登录，默认5条）
     */
    @GetMapping("/latest")
    public Map<String, Object> getLatestNotices(@RequestParam(defaultValue = "5") Integer limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Notice> notices = noticeService.getLatestNotices(limit);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", notices);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取公告详情（无需登录）
     */
    @GetMapping("/{id}")
    public Map<String, Object> getNoticeDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Notice notice = noticeService.getNoticeDetail(id);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", notice);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
