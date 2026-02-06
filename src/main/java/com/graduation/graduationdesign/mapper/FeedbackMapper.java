package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 留言反馈Mapper
 */
@Mapper
public interface FeedbackMapper {

    // 添加反馈
    @Insert("insert into feedback(user_id, type, content, status, create_time, update_time) " +
            "values(#{userId}, #{type}, #{content}, 0, now(), now())")
    void insert(Feedback feedback);

    // 查询所有反馈（管理员用）
    @Select("""
    <script>
    select f.*, u.username from feedback f
    left join user u on f.user_id = u.id
    where 1 = 1
    <if test="status != null">
      and f.status = #{status}
    </if>
    <if test="type != null and type != ''">
      and f.type = #{type}
    </if>
    <if test="keyword != null and keyword != ''">
      and (f.content like concat('%',#{keyword},'%') or u.username like concat('%',#{keyword},'%'))
    </if>
    order by f.create_time desc
    </script>
    """)
    List<Feedback> findAll(@Param("status") Integer status,
                           @Param("type") String type,
                           @Param("keyword") String keyword);

    // 查询我的反馈
    @Select("select * from feedback where user_id = #{userId} order by create_time desc")
    List<Feedback> findByUserId(Long userId);

    @Update("""
    update feedback
    set status = #{status},
        admin_reply = #{adminReply},
        handler_id = #{handlerId},
        handle_time = case when #{status} = 2 then ifnull(handle_time, now()) else handle_time end,
        update_time = now()
    where id = #{id}
    """)
    void updateFeedback(Feedback feedback);

    @Update("""
    update feedback
    set status = #{status},
        handler_id = #{handlerId},
        handle_time = case when #{status} = 2 then ifnull(handle_time, now()) else handle_time end,
        update_time = now()
    where id = #{id}
    """)
    void updateStatus(@Param("id") Long id,
                      @Param("status") Integer status,
                      @Param("handlerId") Long handlerId);
}
