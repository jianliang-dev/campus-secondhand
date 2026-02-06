package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Notice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统公告Mapper
 */
@Mapper
public interface NoticeMapper {

    // 添加公告
    @Insert("insert into notice(title, content, create_time) " +
            "values(#{title}, #{content}, now())")
    void insert(Notice notice);

    // 查询所有公告（按时间倒序）
    @Select("select * from notice order by create_time desc")
    List<Notice> findAll();

    // 查询最新公告（前台用，取前几条）
    @Select("select * from notice order by create_time desc limit #{limit}")
    List<Notice> findLatest(Integer limit);

    // 根据ID查询
    @Select("select * from notice where id = #{id}")
    Notice findById(Long id);

    // 更新公告
    @Update("update notice set title=#{title}, content=#{content} where id=#{id}")
    void update(Notice notice);

    // 删除公告
    @Delete("delete from notice where id = #{id}")
    void delete(Long id);
}
