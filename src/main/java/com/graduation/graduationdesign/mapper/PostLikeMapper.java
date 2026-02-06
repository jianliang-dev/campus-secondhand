package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.PostLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostLikeMapper {

    @Insert("insert into post_like(user_id, post_id, create_time) values(#{userId}, #{postId}, now())")
    void insert(PostLike like);

    @Delete("delete from post_like where user_id = #{userId} and post_id = #{postId}")
    void delete(Long userId, Long postId);

    @Select("select count(*) from post_like where user_id = #{userId} and post_id = #{postId}")
    int exists(Long userId, Long postId);
}
