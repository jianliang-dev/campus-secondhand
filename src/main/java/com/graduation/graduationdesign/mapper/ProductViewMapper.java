package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.ProductView;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品浏览记录Mapper
 */
@Mapper
public interface ProductViewMapper {

    @Insert("insert into product_view(user_id, product_id, create_time) values(#{userId}, #{productId}, now())")
    void insert(ProductView view);

    @Select("select product_id from product_view where user_id = #{userId} order by create_time desc limit #{limit}")
    List<Long> findRecentProductIds(@Param("userId") Long userId, @Param("limit") Integer limit);
}
