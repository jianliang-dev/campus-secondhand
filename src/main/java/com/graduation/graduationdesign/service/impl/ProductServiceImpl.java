package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.entity.Product;
import com.graduation.graduationdesign.entity.ProductView;
import com.graduation.graduationdesign.entity.SearchHistory;
import com.graduation.graduationdesign.mapper.FavoriteMapper;
import com.graduation.graduationdesign.mapper.OrderMapper;
import com.graduation.graduationdesign.mapper.ProductLikeMapper;
import com.graduation.graduationdesign.mapper.ProductMapper;
import com.graduation.graduationdesign.mapper.ProductViewMapper;
import com.graduation.graduationdesign.mapper.SearchHistoryMapper;
import com.graduation.graduationdesign.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品服务实现类（实现ProductService接口）
 */
@Service // 交给Spring管理，Controller可自动注入
// 核心修正：extends → implements（接口必须用实现，不能继承）
public class ProductServiceImpl implements ProductService {

    // 注入商品Mapper
    @Autowired
    private ProductMapper productMapper;

    // 注入搜索历史Mapper
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Autowired
    private ProductViewMapper productViewMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductLikeMapper productLikeMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 发布商品（实现接口方法）
     */
    @Override
    public void publish(Product product, Long userId) {
        // 设置商品发布者ID（从token解析的用户ID）
        product.setUserId(userId);
        // 插入商品数据到数据库
        productMapper.insert(product);
    }

    /**
     * 查询商品列表（只返回上架商品）
     */
    @Override
    public List<Product> list() {
        return productMapper.findAll();
    }

    /**
     * 查询商品详情
     */
    @Override
    public Product detail(Long id) {
        return productMapper.findById(id);
    }

    /**
     * 商品搜索+记录搜索历史
     */
    @Override
    public List<Product> search(String keyword, String token) {
        // 1. 构建搜索历史对象
        SearchHistory history = new SearchHistory();
        history.setKeyword(keyword);

        // 2. 已登录用户：从token解析用户ID并设置
        if (token != null && JwtUtil.validateToken(token)) {
            history.setUserId(JwtUtil.getUserId(token));
        }

        // 3. 插入搜索历史（未登录则user_id为null）
        searchHistoryMapper.insert(history);

        // 4. 模糊搜索商品（只查上架商品）
        return productMapper.search(keyword);
    }

    /**
     * 根据搜索历史推荐商品
     */
    @Override
    public List<Product> recommend(String token, Integer limit) {
        int recommendLimit = (limit == null || limit <= 0) ? 10 : limit;

        // 未登录或token无效：返回最新商品作为兜底
        if (token == null || !JwtUtil.validateToken(token)) {
            return limitProducts(productMapper.findAll(), recommendLimit);
        }

        Long userId = JwtUtil.getUserId(token);
        Set<Long> excludeIds = new HashSet<>();
        List<Long> orderedProductIds = orderMapper.findRecentProductIdsByBuyer(userId, 50);
        if (orderedProductIds != null) {
            excludeIds.addAll(orderedProductIds);
        }
        Map<Long, Double> scoreMap = new HashMap<>();
        Map<Long, Product> productCache = new HashMap<>();

        // 1) 搜索历史关键词推荐
        List<String> keywords = searchHistoryMapper.findRecentKeywords(userId, 10);
        if (keywords != null && !keywords.isEmpty()) {
            for (int i = 0; i < keywords.size(); i++) {
                String keyword = keywords.get(i);
                if (keyword == null || keyword.trim().isEmpty()) {
                    continue;
                }
                double weight = Math.max(2.0, 5.0 - i * 0.5);
                List<Product> products = productMapper.search(keyword.trim());
                addCandidates(products, weight, scoreMap, productCache, excludeIds, userId, 30);
            }
        }

        // 2) 行为偏好：浏览/收藏/点赞/下单 -> 类目权重
        Map<Long, Double> categoryScore = new HashMap<>();
        addCategoryScores(categoryScore, productViewMapper.findRecentProductIds(userId, 30), 2.0);
        addCategoryScores(categoryScore, favoriteMapper.findRecentProductIds(userId, 30), 4.0);
        addCategoryScores(categoryScore, productLikeMapper.findRecentProductIds(userId, 30), 3.0);
        addCategoryScores(categoryScore, orderMapper.findRecentProductIdsByBuyer(userId, 30), 3.0);

        if (!categoryScore.isEmpty()) {
            List<Map.Entry<Long, Double>> categoryEntries = new ArrayList<>(categoryScore.entrySet());
            categoryEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            List<Long> topCategories = new ArrayList<>();
            for (int i = 0; i < categoryEntries.size() && i < 5; i++) {
                topCategories.add(categoryEntries.get(i).getKey());
            }
            List<Product> categoryProducts = productMapper.findByCategoryIds(topCategories, recommendLimit * 5);
            for (Product product : categoryProducts) {
                if (product == null || product.getId() == null || product.getCategoryId() == null) {
                    continue;
                }
                double weight = categoryScore.getOrDefault(product.getCategoryId(), 0.0);
                List<Product> single = new ArrayList<>();
                single.add(product);
                addCandidates(single, weight, scoreMap, productCache, excludeIds, userId, 1);
            }
        }

        List<Map.Entry<Long, Double>> entries = new ArrayList<>(scoreMap.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<Product> result = new ArrayList<>();
        Set<Long> resultIds = new HashSet<>();
        for (Map.Entry<Long, Double> entry : entries) {
            Product product = productCache.get(entry.getKey());
            if (product == null || resultIds.contains(product.getId())) {
                continue;
            }
            result.add(product);
            resultIds.add(product.getId());
            if (result.size() >= recommendLimit) {
                return result;
            }
        }

        // 3) 兜底：最新商品补齐
        List<Product> latest = productMapper.findAll();
        for (Product product : latest) {
            if (product == null || product.getId() == null) {
                continue;
            }
            if (excludeIds.contains(product.getId())) {
                continue;
            }
            if (userId != null && userId.equals(product.getUserId())) {
                continue;
            }
            if (resultIds.contains(product.getId())) {
                continue;
            }
            result.add(product);
            resultIds.add(product.getId());
            if (result.size() >= recommendLimit) {
                break;
            }
        }

        return result;
    }

    private List<Product> limitProducts(List<Product> products, int limit) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        if (products.size() <= limit) {
            return products;
        }
        return new ArrayList<>(products.subList(0, limit));
    }

    private void addCandidates(List<Product> products,
                               double weight,
                               Map<Long, Double> scoreMap,
                               Map<Long, Product> productCache,
                               Set<Long> excludeIds,
                               Long userId,
                               int maxCount) {
        if (products == null || products.isEmpty()) {
            return;
        }
        int count = 0;
        for (Product product : products) {
            if (product == null || product.getId() == null) {
                continue;
            }
            if (excludeIds != null && excludeIds.contains(product.getId())) {
                continue;
            }
            if (userId != null && userId.equals(product.getUserId())) {
                continue;
            }
            scoreMap.merge(product.getId(), weight, Double::sum);
            productCache.putIfAbsent(product.getId(), product);
            count++;
            if (count >= maxCount) {
                break;
            }
        }
    }

    private void addCategoryScores(Map<Long, Double> categoryScore, List<Long> productIds, double baseWeight) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        List<Product> products = productMapper.findByIds(productIds);
        if (products == null || products.isEmpty()) {
            return;
        }
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : products) {
            if (product != null && product.getId() != null) {
                productMap.put(product.getId(), product);
            }
        }
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Product product = productMap.get(productId);
            if (product == null || product.getCategoryId() == null) {
                continue;
            }
            double weight = Math.max(1.0, baseWeight - i * 0.2);
            categoryScore.merge(product.getCategoryId(), weight, Double::sum);
        }
    }

    /**
     * 查询我的商品列表
     */
    @Override
    public List<Product> myProducts(Long userId) {
        return productMapper.findByUserId(userId);
    }

    /**
     * 更新商品信息
     */
    @Override
    public void update(Product product, Long userId) {
        // 1. 查询商品是否存在
        Product existProduct = productMapper.findById(product.getId());
        if (existProduct == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 校验权限：只有商品发布者才能修改
        if (!existProduct.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该商品");
        }

        // 3. 更新商品信息
        productMapper.update(product);
    }

    /**
     * 删除商品
     */
    @Override
    public void delete(Long productId, Long userId) {
        // 1. 查询商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 校验权限：只有商品发布者才能删除
        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该商品");
        }

        // 3. 删除商品
        productMapper.delete(productId);
    }

    /**
     * 更新商品状态（上下架）
     */
    @Override
    public void updateStatus(Long productId, Integer status, Long userId) {
        // 1. 查询商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 校验权限：只有商品发布者才能修改状态
        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该商品状态");
        }

        // 3. 更新状态
        productMapper.updateStatus(productId, status);
    }

    @Override
    public void recordView(Long productId, Long userId) {
        if (productId == null || userId == null) {
            return;
        }
        ProductView view = new ProductView();
        view.setProductId(productId);
        view.setUserId(userId);
        productViewMapper.insert(view);
    }
}