package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Category;
import com.graduation.graduationdesign.mapper.CategoryMapper;
import com.graduation.graduationdesign.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getEnabledCategories() {
        return categoryMapper.findAllEnabled();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public void addCategory(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.delete(id);
    }
}
