package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    @Select("select * from category where status = 1 order by sort_order asc, create_time desc")
    List<Category> findAllEnabled();

    @Select("select * from category order by sort_order asc, create_time desc")
    List<Category> findAll();

    @Insert("insert into category(name, status, sort_order, create_time, update_time) " +
            "values(#{name}, #{status}, #{sortOrder}, now(), now())")
    void insert(Category category);

    @Update("update category set name=#{name}, status=#{status}, sort_order=#{sortOrder}, update_time=now() where id=#{id}")
    void update(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);
}
