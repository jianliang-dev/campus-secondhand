package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Post;
import com.graduation.graduationdesign.vo.PostVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper {

    // 发布帖子
    @Insert("insert into post(user_id, circle_name, title, content, status, create_time) " +
            "values(#{userId}, #{circleName}, #{title}, #{content}, 1, now())")
    void insert(Post post);

    // 帖子列表（包含用户信息、统计数据）
    @Select("""
    <script>
    select p.id, p.user_id, p.circle_name, p.title, p.content, p.status, p.create_time,
           p.view_count, p.like_count,
           (select count(*) from comment c where c.target_type = 2 and c.target_id = p.id) as comment_count,
           (select max(c.create_time) from comment c where c.target_type = 2 and c.target_id = p.id) as last_reply_time,
           u.username, u.avatar
    from post p
    left join user u on p.user_id = u.id
    where p.status = 1
    <if test="circleName != null and circleName != ''">
      and p.circle_name = #{circleName}
    </if>
    <choose>
      <when test="sort == 'reply'">
        order by case when last_reply_time is null then p.create_time else last_reply_time end desc
      </when>
      <otherwise>
        order by p.create_time desc
      </otherwise>
    </choose>
    limit #{offset}, #{size}
    </script>
    """)
    List<PostVO> findPaged(@Param("circleName") String circleName,
                           @Param("sort") String sort,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size);

    @Select("""
    <script>
    select count(*) from post p
    where p.status = 1
    <if test="circleName != null and circleName != ''">
      and p.circle_name = #{circleName}
    </if>
    </script>
    """)
    Integer countPosts(@Param("circleName") String circleName);

    // 帖子详情（包含用户信息、统计数据）
    @Select("""
    select p.id, p.user_id, p.circle_name, p.title, p.content, p.status, p.create_time,
           p.view_count, p.like_count,
           (select count(*) from comment c where c.target_type = 2 and c.target_id = p.id) as comment_count,
           (select max(c.create_time) from comment c where c.target_type = 2 and c.target_id = p.id) as last_reply_time,
           u.username, u.avatar
    from post p
    left join user u on p.user_id = u.id
    where p.id = #{id}
    """)
    PostVO findById(Long id);

    // 查询我的帖子列表
    @Select("select * from post where user_id = #{userId} order by create_time desc")
    List<Post> findByUserId(Long userId);

    // 更新帖子
    @Update("update post set circle_name=#{circleName}, title=#{title}, content=#{content} " +
            "where id=#{id}")
    void update(Post post);

    // 删除帖子
    @Delete("delete from post where id = #{id}")
    void delete(Long id);

    // 更新帖子状态
    @Update("update post set status=#{status} where id=#{id}")
    void updateStatus(Long id, Integer status);

    @Update("update post set view_count = view_count + 1 where id = #{id}")
    void incrementViewCount(Long id);

    @Update("update post set last_reply_time = now() where id = #{id}")
    void updateLastReplyTime(Long id);

    @Update("update post set like_count = ifnull(like_count, 0) + 1 where id = #{id}")
    void incrementLikeCount(Long id);

    @Update("update post set like_count = case when ifnull(like_count, 0) > 0 then ifnull(like_count, 0) - 1 else 0 end where id = #{id}")
    void decrementLikeCount(Long id);

    // 热门帖子（近7天，按热度排序）
    @Select("""
    select p.id, p.user_id, p.circle_name, p.title, p.content, p.status, p.create_time,
           p.view_count, p.like_count,
           (select count(*) from comment c where c.target_type = 2 and c.target_id = p.id) as comment_count,
           (ifnull(p.view_count, 0)
             + (select count(*) from comment c where c.target_type = 2 and c.target_id = p.id) * 5
             + ifnull(p.like_count, 0) * 3) as hot_score,
           u.username, u.avatar
    from post p
    left join user u on p.user_id = u.id
    where p.status = 1 and p.create_time >= date_sub(now(), interval 7 day)
    order by hot_score desc, p.create_time desc
    limit #{limit}
    """)
    List<PostVO> findHotPosts(@Param("limit") Integer limit);

    // 查询所有帖子（管理员用，包括隐藏的帖子）
    @Select("select * from post order by create_time desc")
    List<Post> findAllForAdmin();
}
