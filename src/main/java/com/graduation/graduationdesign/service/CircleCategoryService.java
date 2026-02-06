package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.CircleCategory;

import java.util.List;

/**
 * 社区分类服务接口
 */
public interface CircleCategoryService {
    List<CircleCategory> getEnabledCategories();
    List<CircleCategory> getAllCategories();
    void addCategory(CircleCategory category);
    void updateCategory(CircleCategory category);
    void deleteCategory(Long id);
}
