package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.CircleCategory;
import com.graduation.graduationdesign.mapper.CircleCategoryMapper;
import com.graduation.graduationdesign.service.CircleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社区分类服务实现类
 */
@Service
public class CircleCategoryServiceImpl implements CircleCategoryService {

    @Autowired
    private CircleCategoryMapper circleCategoryMapper;

    @Override
    public List<CircleCategory> getEnabledCategories() {
        return circleCategoryMapper.findAllEnabled();
    }

    @Override
    public List<CircleCategory> getAllCategories() {
        return circleCategoryMapper.findAll();
    }

    @Override
    public void addCategory(CircleCategory category) {
        circleCategoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CircleCategory category) {
        circleCategoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        circleCategoryMapper.delete(id);
    }
}
