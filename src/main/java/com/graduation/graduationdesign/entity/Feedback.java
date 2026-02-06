package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 留言反馈实体类
 */
@Data
public class Feedback {
    private Long id;
    private Long userId;      // 反馈用户ID
    private String username;  // 用户昵称（联表查询）
    private String type;      // 反馈类型
    private String content;   // 反馈内容
    private Integer status;   // 状态：0-未处理 1-处理中 2-已处理
    private String adminReply;// 管理员回复
    private Long handlerId;   // 处理人ID
    private Date handleTime;  // 处理时间
    private Date createTime;  // 反馈时间
    private Date updateTime;  // 更新时间
}
