package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Product;
import java.util.List;

/**
 * 商品服务接口（定义规范）
 */
public interface ProductService {
    void publish(Product product, Long userId);
    List<Product> list();
    Product detail(Long id);
    List<Product> search(String keyword, String token);
    List<Product> recommend(String token, Integer limit);
    List<Product> myProducts(Long userId);
    void update(Product product, Long userId);
    void delete(Long productId, Long userId);
    void updateStatus(Long productId, Integer status, Long userId);
    void recordView(Long productId, Long userId);
}