package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.CircleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 社区分类Mapper
 */
@Mapper
public interface CircleCategoryMapper {

    @Select("select * from circle_category where status = 1 order by sort_order asc, create_time desc")
    List<CircleCategory> findAllEnabled();

    @Select("select * from circle_category order by sort_order asc, create_time desc")
    List<CircleCategory> findAll();

    @Insert("insert into circle_category(name, status, sort_order, create_time, update_time) " +
            "values(#{name}, #{status}, #{sortOrder}, now(), now())")
    void insert(CircleCategory category);

    @Update("update circle_category set name=#{name}, status=#{status}, sort_order=#{sortOrder}, update_time=now() where id=#{id}")
    void update(CircleCategory category);

    @Delete("delete from circle_category where id = #{id}")
    void delete(Long id);
}
