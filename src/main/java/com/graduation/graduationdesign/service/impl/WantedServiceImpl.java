package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Wanted;
import com.graduation.graduationdesign.mapper.WantedMapper;
import com.graduation.graduationdesign.service.WantedService;
import com.graduation.graduationdesign.vo.WantedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 求购服务实现类
 */
@Service
public class WantedServiceImpl implements WantedService {

    @Autowired
    private WantedMapper wantedMapper;

    @Override
    public void publishWanted(Wanted wanted, Long userId) {
        wanted.setUserId(userId);
        wantedMapper.insert(wanted);
    }

    @Override
    public List<WantedVO> getAllWanted(Long categoryId, Integer status, String sort) {
        String sortKey = sort == null ? "latest" : sort;
        return wantedMapper.findAll(categoryId, status, sortKey);
    }

    @Override
    public WantedVO getWantedDetail(Long id) {
        return wantedMapper.findById(id);
    }

    @Override
    public List<Wanted> getMyWanted(Long userId) {
        return wantedMapper.findByUserId(userId);
    }

    @Override
    public void updateWanted(Wanted wanted, Long userId) {
        // 查询求购信息是否存在
        WantedVO existWanted = wantedMapper.findById(wanted.getId());
        if (existWanted == null) {
            throw new RuntimeException("求购信息不存在");
        }
        // 校验权限
        if (!existWanted.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该求购信息");
        }
        wantedMapper.update(wanted);
    }

    @Override
    public void deleteWanted(Long id, Long userId) {
        // 查询求购信息是否存在
        WantedVO existWanted = wantedMapper.findById(id);
        if (existWanted == null) {
            throw new RuntimeException("求购信息不存在");
        }
        // 校验权限
        if (!existWanted.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该求购信息");
        }
        wantedMapper.delete(id);
    }

    @Override
    public void updateWantedStatus(Long id, Integer status, Long userId) {
        // 查询求购信息是否存在
        WantedVO existWanted = wantedMapper.findById(id);
        if (existWanted == null) {
            throw new RuntimeException("求购信息不存在");
        }
        // 校验权限
        if (!existWanted.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该求购信息状态");
        }
        wantedMapper.updateStatus(id, status);
    }
}
