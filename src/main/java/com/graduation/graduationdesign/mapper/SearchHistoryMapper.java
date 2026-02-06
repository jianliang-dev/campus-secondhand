package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.SearchHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper // 交给MyBatis管理，必须加
public interface SearchHistoryMapper {

    // 插入搜索历史：user_id可为null（未登录），create_time用now()自动生成
    @Insert("insert into search_history(user_id, keyword, create_time) " +
            "values(#{userId}, #{keyword}, now())")
    void insert(SearchHistory history);

    // 查询用户最近的搜索关键词（按时间倒序）
    @Select("select keyword from search_history " +
            "where user_id = #{userId} and keyword is not null and keyword <> '' " +
            "order by create_time desc limit #{limit}")
    List<String> findRecentKeywords(@Param("userId") Long userId, @Param("limit") Integer limit);
}