package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    List<Category> getEnabledCategories();
    List<Category> getAllCategories();
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Long id);
}
