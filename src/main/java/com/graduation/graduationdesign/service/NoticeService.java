package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Notice;

import java.util.List;

/**
 * 系统公告服务接口
 */
public interface NoticeService {
    List<Notice> getAllNotices();
    List<Notice> getLatestNotices(Integer limit);
    Notice getNoticeDetail(Long id);
    void addNotice(Notice notice);
    void updateNotice(Notice notice);
    void deleteNotice(Long id);
}
