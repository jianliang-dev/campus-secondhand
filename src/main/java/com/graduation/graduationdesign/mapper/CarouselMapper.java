package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Carousel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 轮播图Mapper
 */
@Mapper
public interface CarouselMapper {

    // 添加轮播图
    @Insert("insert into carousel(image_url, link_url, sort_order, status, create_time) " +
            "values(#{imageUrl}, #{linkUrl}, #{sortOrder}, #{status}, now())")
    void insert(Carousel carousel);

    // 查询所有启用的轮播图（前台用，按排序值排序）
    @Select("select * from carousel where status = 1 order by sort_order asc, create_time desc")
    List<Carousel> findAllEnabled();

    // 查询所有轮播图（管理员用）
    @Select("select * from carousel order by sort_order asc, create_time desc")
    List<Carousel> findAll();

    // 根据ID查询
    @Select("select * from carousel where id = #{id}")
    Carousel findById(Long id);

    // 更新轮播图
    @Update("update carousel set image_url=#{imageUrl}, link_url=#{linkUrl}, " +
            "sort_order=#{sortOrder}, status=#{status} where id=#{id}")
    void update(Carousel carousel);

    // 删除轮播图
    @Delete("delete from carousel where id = #{id}")
    void delete(Long id);
}
