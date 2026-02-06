package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Notice;
import com.graduation.graduationdesign.mapper.NoticeMapper;
import com.graduation.graduationdesign.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统公告服务实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> getAllNotices() {
        return noticeMapper.findAll();
    }

    @Override
    public List<Notice> getLatestNotices(Integer limit) {
        return noticeMapper.findLatest(limit);
    }

    @Override
    public Notice getNoticeDetail(Long id) {
        return noticeMapper.findById(id);
    }

    @Override
    public void addNotice(Notice notice) {
        noticeMapper.insert(notice);
    }

    @Override
    public void updateNotice(Notice notice) {
        noticeMapper.update(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeMapper.delete(id);
    }
}
