package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Wanted;
import com.graduation.graduationdesign.vo.WantedVO;

import java.util.List;

/**
 * 求购服务接口
 */
public interface WantedService {
    void publishWanted(Wanted wanted, Long userId);
    List<WantedVO> getAllWanted(Long categoryId, Integer status, String sort);
    WantedVO getWantedDetail(Long id);
    List<Wanted> getMyWanted(Long userId);
    void updateWanted(Wanted wanted, Long userId);
    void deleteWanted(Long id, Long userId);
    void updateWantedStatus(Long id, Integer status, Long userId);
}
