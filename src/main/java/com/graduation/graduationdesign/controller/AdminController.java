package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.common.AdminUtil;
import com.graduation.graduationdesign.entity.*;
import com.graduation.graduationdesign.mapper.*;
import com.graduation.graduationdesign.service.CarouselService;
import com.graduation.graduationdesign.service.CategoryService;
import com.graduation.graduationdesign.service.CircleCategoryService;
import com.graduation.graduationdesign.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员后台控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private WantedMapper wantedMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CircleCategoryService circleCategoryService;

    /**
     * 获取所有用户列表（管理员）
     */
    @GetMapping("/user/list")
    public Map<String, Object> getAllUsers(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<User> users = userMapper.findAll();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", users);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户状态（管理员）
     */
    @PostMapping("/user/updateStatus")
    public Map<String, Object> updateUserStatus(@RequestParam Long userId,
                                                 @RequestParam Integer status,
                                                 @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            userMapper.updateStatus(userId, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有商品列表（管理员，包括下架商品）
     */
    @GetMapping("/product/list")
    public Map<String, Object> getAllProducts(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Product> products = productMapper.findAllForAdmin();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", products);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员更新商品状态（审核、上下架）
     */
    @PostMapping("/product/updateStatus")
    public Map<String, Object> adminUpdateProductStatus(@RequestParam Long productId,
                                                         @RequestParam Integer status,
                                                         @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            productMapper.updateStatus(productId, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有订单列表（管理员）
     */
    @GetMapping("/order/list")
    public Map<String, Object> getAllOrders(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<OrderInfo> orders = orderMapper.findAllForAdmin();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", orders);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员更新订单状态
     */
    @PostMapping("/order/updateStatus")
    public Map<String, Object> adminUpdateOrderStatus(@RequestParam Long orderId,
                                                       @RequestParam Integer orderStatus,
                                                       @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            orderMapper.updateOrderStatus(orderId, orderStatus);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有帖子列表（管理员）
     */
    @GetMapping("/post/list")
    public Map<String, Object> getAllPosts(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Post> posts = postMapper.findAllForAdmin();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", posts);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员更新帖子状态
     */
    @PostMapping("/post/updateStatus")
    public Map<String, Object> adminUpdatePostStatus(@RequestParam Long postId,
                                                      @RequestParam Integer status,
                                                      @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            postMapper.updateStatus(postId, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有求购列表（管理员）
     */
    @GetMapping("/wanted/list")
    public Map<String, Object> getAllWanted(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Wanted> wantedList = wantedMapper.findAllForAdmin();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", wantedList);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员更新求购状态
     */
    @PostMapping("/wanted/updateStatus")
    public Map<String, Object> adminUpdateWantedStatus(@RequestParam Long wantedId,
                                                        @RequestParam Integer status,
                                                        @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            wantedMapper.updateStatus(wantedId, status);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有反馈列表（管理员）
     */
    @GetMapping("/feedback/list")
    public Map<String, Object> getAllFeedback(@RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) String keyword,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Feedback> feedbackList = feedbackMapper.findAll(status, type, keyword);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", feedbackList);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员更新反馈状态/回复
     */
    @PostMapping("/feedback/update")
    public Map<String, Object> updateFeedback(@RequestBody Feedback feedback,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long adminId = AdminUtil.getAdminId(token, userMapper);
            feedback.setHandlerId(adminId);
            feedbackMapper.updateFeedback(feedback);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 管理员快速更新反馈状态
     */
    @PostMapping("/feedback/updateStatus")
    public Map<String, Object> updateFeedbackStatus(@RequestParam Long id,
                                                    @RequestParam Integer status,
                                                    @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long adminId = AdminUtil.getAdminId(token, userMapper);
            feedbackMapper.updateStatus(id, status, adminId);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 轮播图管理 - 获取所有轮播图（管理员）
     */
    @GetMapping("/carousel/list")
    public Map<String, Object> getAllCarousels(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Carousel> carousels = carouselService.getAllCarousels();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", carousels);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 轮播图管理 - 添加轮播图（管理员）
     */
    @PostMapping("/carousel/add")
    public Map<String, Object> addCarousel(@RequestBody Carousel carousel,
                                            @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            carouselService.addCarousel(carousel);
            result.put("code", 200);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 轮播图管理 - 更新轮播图（管理员）
     */
    @PostMapping("/carousel/update")
    public Map<String, Object> updateCarousel(@RequestBody Carousel carousel,
                                               @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            carouselService.updateCarousel(carousel);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 轮播图管理 - 删除轮播图（管理员）
     */
    @PostMapping("/carousel/delete")
    public Map<String, Object> deleteCarousel(@RequestParam Long id,
                                               @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            carouselService.deleteCarousel(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分类管理 - 获取所有分类（管理员）
     */
    @GetMapping("/category/list")
    public Map<String, Object> getAllCategories(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<Category> categories = categoryService.getAllCategories();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", categories);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分类管理 - 添加分类（管理员）
     */
    @PostMapping("/category/add")
    public Map<String, Object> addCategory(@RequestBody Category category,
                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            categoryService.addCategory(category);
            result.put("code", 200);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分类管理 - 更新分类（管理员）
     */
    @PostMapping("/category/update")
    public Map<String, Object> updateCategory(@RequestBody Category category,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            categoryService.updateCategory(category);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 分类管理 - 删除分类（管理员）
     */
    @PostMapping("/category/delete")
    public Map<String, Object> deleteCategory(@RequestParam Long id,
                                              @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            categoryService.deleteCategory(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 社区分类管理 - 获取所有分类（管理员）
     */
    @GetMapping("/circleCategory/list")
    public Map<String, Object> getAllCircleCategories(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            List<CircleCategory> categories = circleCategoryService.getAllCategories();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", categories);
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 社区分类管理 - 添加分类（管理员）
     */
    @PostMapping("/circleCategory/add")
    public Map<String, Object> addCircleCategory(@RequestBody CircleCategory category,
                                                 @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            circleCategoryService.addCategory(category);
            result.put("code", 200);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 社区分类管理 - 更新分类（管理员）
     */
    @PostMapping("/circleCategory/update")
    public Map<String, Object> updateCircleCategory(@RequestBody CircleCategory category,
                                                    @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            circleCategoryService.updateCategory(category);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 社区分类管理 - 删除分类（管理员）
     */
    @PostMapping("/circleCategory/delete")
    public Map<String, Object> deleteCircleCategory(@RequestParam Long id,
                                                    @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            circleCategoryService.deleteCategory(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 系统公告管理 - 添加公告（管理员）
     */
    @PostMapping("/notice/add")
    public Map<String, Object> addNotice(@RequestBody Notice notice,
                                          @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            noticeService.addNotice(notice);
            result.put("code", 200);
            result.put("message", "添加成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 系统公告管理 - 更新公告（管理员）
     */
    @PostMapping("/notice/update")
    public Map<String, Object> updateNotice(@RequestBody Notice notice,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            noticeService.updateNotice(notice);
            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 系统公告管理 - 删除公告（管理员）
     */
    @PostMapping("/notice/delete")
    public Map<String, Object> deleteNotice(@RequestParam Long id,
                                             @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            AdminUtil.getAdminId(token, userMapper);
            noticeService.deleteNotice(id);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (RuntimeException e) {
            result.put("code", 403);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
