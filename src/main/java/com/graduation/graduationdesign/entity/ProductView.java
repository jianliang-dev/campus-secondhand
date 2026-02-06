package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 商品浏览记录实体类
 */
@Data
public class ProductView {
    private Long id;
    private Long userId;
    private Long productId;
    private Date createTime;
}
