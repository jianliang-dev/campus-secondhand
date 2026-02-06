package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    @Select("select * from user where id = #{id}")
    User findById(Long id);

    @Insert("insert into user(username, password, phone, avatar, signature, role, status, create_time, update_time) " +
            "values(#{username}, #{password}, #{phone}, #{avatar}, #{signature}, 0, 1, now(), now())")
    void insert(User user);

    @Update("update user set password = #{password}, update_time = now() where id = #{id}")
    void updatePassword(Long id, String password);

    @Update("update user set phone = #{phone}, avatar = #{avatar}, signature = #{signature}, update_time = now() where id = #{id}")
    void updateProfile(Long id, String phone, String avatar, String signature);

    // 查询所有用户（管理员用）
    @Select("select * from user order by create_time desc")
    List<User> findAll();

    // 更新用户状态（管理员用）
    @Update("update user set status = #{status}, update_time = now() where id = #{id}")
    void updateStatus(Long id, Integer status);
}
