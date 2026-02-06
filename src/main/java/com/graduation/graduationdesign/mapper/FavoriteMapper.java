package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Favorite;
import com.graduation.graduationdesign.vo.FavoriteProductVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    // 新增收藏
    @Insert("insert into favorite(user_id, product_id, create_time) values(#{userId}, #{productId}, now())")
    void insert(Favorite favorite);

    // 取消收藏
    @Delete("delete from favorite where user_id = #{userId} and product_id = #{productId}")
    void delete(Long userId, Long productId);

    // 判断是否收藏（count>0则已收藏）
    @Select("select count(*) from favorite where user_id = #{userId} and product_id = #{productId}")
    int exists(Long userId, Long productId);

    // 收藏列表（按收藏时间倒序）
    @Select("""
    select f.id as favorite_id,
           f.create_time as favorite_time,
           p.id as product_id,
           p.title,
           p.price,
           p.cover_img,
           p.status,
           p.create_time,
           u.id as seller_id,
           u.username as seller_name,
           u.avatar as seller_avatar,
           case
             when exists(select 1 from order_info o where o.product_id = p.id and o.pay_status = 1) then 1
             else 0
           end as sold_flag
    from favorite f
    left join product p on f.product_id = p.id
    left join user u on p.user_id = u.id
    where f.user_id = #{userId}
    order by f.create_time desc
    """)
    List<FavoriteProductVO> listByUserId(Long userId);

    // 最近收藏的商品ID
    @Select("select product_id from favorite where user_id = #{userId} order by create_time desc limit #{limit}")
    List<Long> findRecentProductIds(Long userId, Integer limit);
}