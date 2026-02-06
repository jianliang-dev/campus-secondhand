package com.graduation.graduationdesign.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户实体类（与数据库user表字段对齐）
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private String signature;
    private Integer role;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
