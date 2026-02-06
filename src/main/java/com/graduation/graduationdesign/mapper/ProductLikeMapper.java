package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.ProductLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductLikeMapper {

    // 新增点赞
    @Insert("insert into product_like(user_id, product_id, create_time) values(#{userId}, #{productId}, now())")
    void insert(ProductLike like);

    // 取消点赞
    @Delete("delete from product_like where user_id = #{userId} and product_id = #{productId}")
    void delete(Long userId, Long productId);

    // 判断是否点赞（count>0则已点赞）
    @Select("select count(*) from product_like where user_id = #{userId} and product_id = #{productId}")
    int exists(Long userId, Long productId);

    // 最近点赞的商品ID
    @Select("select product_id from product_like where user_id = #{userId} order by create_time desc limit #{limit}")
    List<Long> findRecentProductIds(Long userId, Integer limit);
}