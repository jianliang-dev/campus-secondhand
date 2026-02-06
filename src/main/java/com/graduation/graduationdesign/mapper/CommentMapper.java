package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Comment;
import com.graduation.graduationdesign.vo.CommentVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper {

    // 添加评论
    @Insert("insert into comment(user_id, target_id, target_type, content, create_time) " +
            "values(#{userId}, #{targetId}, #{targetType}, #{content}, now())")
    void insert(Comment comment);

    // 根据目标ID和类型查询评论列表（包含用户信息）
    @Select("select c.id, c.user_id, c.target_id, c.target_type, c.content, c.create_time, " +
            "u.username, u.avatar from comment c " +
            "left join user u on c.user_id = u.id " +
            "where c.target_id = #{targetId} and c.target_type = #{targetType} " +
            "order by c.create_time desc")
    List<CommentVO> findByTarget(Long targetId, Integer targetType);

    // 根据用户ID查询评论列表
    @Select("select * from comment where user_id = #{userId} order by create_time desc")
    List<Comment> findByUserId(Long userId);

    // 根据ID查询评论
    @Select("select * from comment where id = #{id}")
    Comment findById(Long id);

    // 删除评论
    @Delete("delete from comment where id = #{id}")
    void delete(Long id);
}
