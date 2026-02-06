package com.graduation.graduationdesign.entity;

import lombok.Data;
import java.util.Date;

/**
 * 收货地址实体类
 */
@Data
public class Address {
    private Long id;
    private Long userId;
    private String receiverName;   // 收货人姓名
    private String receiverPhone;  // 收货人电话
    private Integer addressType;   // 地址类型：1-校内 2-校外
    private String school;         // 学校
    private String campus;         // 校区
    private String building;       // 宿舍楼/驿站
    private String room;           // 寝室号/取件点备注
    private String province;       // 省
    private String city;           // 市
    private String detailAddress;  // 详细地址
    private String pickupRemark;   // 取件备注
    private Integer isDefault;     // 是否默认地址：1-是 0-否
    private Date createTime;
}
