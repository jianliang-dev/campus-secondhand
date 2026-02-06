package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Address;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 收货地址Mapper
 */
@Mapper
public interface AddressMapper {

    // 添加地址
    @Insert("insert into address(user_id, receiver_name, receiver_phone, address_type, school, campus, building, room, " +
            "province, city, detail_address, pickup_remark, is_default, create_time) " +
            "values(#{userId}, #{receiverName}, #{receiverPhone}, #{addressType}, #{school}, #{campus}, #{building}, #{room}, " +
            "#{province}, #{city}, #{detailAddress}, #{pickupRemark}, #{isDefault}, now())")
    void insert(Address address);

    // 查询用户的所有地址
    @Select("select * from address where user_id = #{userId} order by is_default desc, create_time desc")
    List<Address> findByUserId(Long userId);

    // 根据ID查询地址
    @Select("select * from address where id = #{id}")
    Address findById(Long id);

    // 更新地址
    @Update("update address set receiver_name=#{receiverName}, receiver_phone=#{receiverPhone}, " +
            "address_type=#{addressType}, school=#{school}, campus=#{campus}, building=#{building}, room=#{room}, " +
            "province=#{province}, city=#{city}, detail_address=#{detailAddress}, pickup_remark=#{pickupRemark}, " +
            "is_default=#{isDefault} where id=#{id}")
    void update(Address address);

    // 删除地址
    @Delete("delete from address where id = #{id}")
    void delete(Long id);

    // 取消用户的所有默认地址
    @Update("update address set is_default = 0 where user_id = #{userId}")
    void cancelAllDefault(Long userId);

    // 设置默认地址
    @Update("update address set is_default = 1 where id = #{id}")
    void setDefault(Long id);
}
