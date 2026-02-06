package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Wanted;
import com.graduation.graduationdesign.vo.WantedVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 求购Mapper
 */
@Mapper
public interface WantedMapper {

    // 发布求购信息
    @Insert("insert into wanted(user_id, title, category_id, expected, description, budget, status, create_time) " +
            "values(#{userId}, #{title}, #{categoryId}, #{expected}, #{description}, #{budget}, 1, now())")
    void insert(Wanted wanted);

    // 求购列表（包含用户信息、分类信息）
    @Select("""
    <script>
    select w.id, w.user_id, w.title, w.category_id, w.expected, w.description, w.budget, w.status, w.create_time,
           u.username, u.avatar, c.name as category_name
    from wanted w
    left join user u on w.user_id = u.id
    left join category c on w.category_id = c.id
    where 1 = 1
    <if test="categoryId != null">
      and w.category_id = #{categoryId}
    </if>
    <if test="status != null">
      and w.status = #{status}
    </if>
    <choose>
      <when test="sort == 'budgetAsc'">
        order by w.budget asc, w.create_time desc
      </when>
      <when test="sort == 'budgetDesc'">
        order by w.budget desc, w.create_time desc
      </when>
      <otherwise>
        order by w.create_time desc
      </otherwise>
    </choose>
    </script>
    """)
    List<WantedVO> findAll(@Param("categoryId") Long categoryId,
                           @Param("status") Integer status,
                           @Param("sort") String sort);

    // 求购详情（包含用户信息）
    @Select("select w.id, w.user_id, w.title, w.category_id, w.expected, w.description, w.budget, w.status, w.create_time, " +
            "u.username, u.avatar, c.name as category_name from wanted w " +
            "left join user u on w.user_id = u.id " +
            "left join category c on w.category_id = c.id " +
            "where w.id = #{id}")
    WantedVO findById(Long id);

    // 查询我的求购列表
    @Select("select * from wanted where user_id = #{userId} order by create_time desc")
    List<Wanted> findByUserId(Long userId);

    // 更新求购信息
    @Update("update wanted set title=#{title}, category_id=#{categoryId}, expected=#{expected}, description=#{description}, budget=#{budget} " +
            "where id=#{id}")
    void update(Wanted wanted);

    // 删除求购信息
    @Delete("delete from wanted where id = #{id}")
    void delete(Long id);

    // 更新求购状态
    @Update("update wanted set status=#{status} where id=#{id}")
    void updateStatus(Long id, Integer status);

    // 查询所有求购（管理员用，包括已关闭的）
    @Select("select * from wanted order by create_time desc")
    List<Wanted> findAllForAdmin();
}
