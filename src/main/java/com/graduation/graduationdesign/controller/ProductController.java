package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.entity.Product;
import com.graduation.graduationdesign.entity.User;
import com.graduation.graduationdesign.mapper.UserMapper;
import com.graduation.graduationdesign.common.JwtUtil;
import com.graduation.graduationdesign.service.ProductService;
import com.graduation.graduationdesign.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController // 标识为REST接口，返回JSON
@RequestMapping("/product") // 接口统一前缀
public class ProductController {

    @Autowired // 自动注入ProductService接口（Spring会找对应的实现类）
    private ProductService productService;

    @Autowired
    private UserMapper userMapper;

    // 发布商品接口（需要登录，校验token）
    @PostMapping("/publish") // POST请求，路径：/product/publish
    public String publish(@RequestBody Product product,
                          @RequestHeader("Authorization") String token) {

        // 1. 校验token是否有效（未登录/过期/篡改则返回未登录）
        if (!JwtUtil.validateToken(token)) {
            return "未登录";
        }

        // 2. 从token解析出登录用户ID
        Long userId = JwtUtil.getUserId(token);

        // 3. 调用服务层发布商品
        productService.publish(product, userId);
        return "发布成功";
    }

    // 商品列表接口（无需登录）
    @GetMapping("/list") // GET请求，路径：/product/list
    public List<Product> list() {
        return productService.list();
    }

    // 商品详情接口（无需登录）
    @GetMapping("/{id}") // GET请求，路径：/product/1（1为商品ID）
    public ProductDetailVO detail(@PathVariable Long id,
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        Product product = productService.detail(id);
        if (product == null) {
            return null;
        }
        if (token != null && JwtUtil.validateToken(token)) {
            Long userId = JwtUtil.getUserId(token);
            productService.recordView(id, userId);
        }
        User seller = userMapper.findById(product.getUserId());
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setUserId(product.getUserId());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setCategoryId(product.getCategoryId());
        vo.setCoverImg(product.getCoverImg());
        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        if (seller != null) {
            vo.setSellerId(seller.getId());
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        return vo;
    }

    // 新增：商品搜索接口
    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword,
                                @RequestHeader(value = "Authorization", required = false) String token) {

        return productService.search(keyword, token);
    }

    // 猜你喜欢推荐（基于搜索历史，未登录则返回最新商品）
    @GetMapping("/recommend")
    public List<Product> recommend(@RequestParam(required = false) Integer limit,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        return productService.recommend(token, limit);
    }

    // 我的商品列表（需要登录）
    @GetMapping("/my")
    public List<Product> myProducts(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.validateToken(token)) {
            throw new RuntimeException("未登录");
        }
        Long userId = JwtUtil.getUserId(token);
        return productService.myProducts(userId);
    }

    // 编辑商品（需要登录）
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Product product,
                                      @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            productService.update(product, userId);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 删除商品（需要登录）
    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long productId,
                                      @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            productService.delete(productId, userId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 上下架商品（需要登录）
    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestParam Long productId,
                                            @RequestParam Integer status,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            productService.updateStatus(productId, status, userId);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 上传商品封面（需要登录）
    @PostMapping(value = "/uploadCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadCover(@RequestParam("file") MultipartFile file,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!JwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("message", "未登录");
                return result;
            }
            if (file == null || file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "请选择文件");
                return result;
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("code", 400);
                result.put("message", "仅支持图片文件");
                return result;
            }
            Long userId = JwtUtil.getUserId(token);
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            if (ext.isEmpty()) {
                ext = ".jpg";
            }
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "products");
            Files.createDirectories(uploadDir);
            String filename = userId + "_" + UUID.randomUUID().toString().replace("-", "") + ext;
            Path targetPath = uploadDir.resolve(filename);
            file.transferTo(targetPath.toFile());
            String url = "/uploads/products/" + filename;
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败：" + e.getMessage());
        }
        return result;
    }

}