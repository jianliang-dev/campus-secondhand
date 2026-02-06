package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.OrderInfo;
import com.graduation.graduationdesign.vo.BuyerOrderListVO;
import com.graduation.graduationdesign.vo.SellerOrderListVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    // 创建订单：order_status/pay_status默认0，create_time用now()
    @Insert("insert into order_info(order_no,buyer_id,seller_id,product_id,total_price,order_status,pay_status,create_time) " +
            "values(#{orderNo},#{buyerId},#{sellerId},#{productId},#{totalPrice},0,0,now())")
    void insert(OrderInfo order);

    // 查询我的订单（按创建时间倒序）
    @Select("select * from order_info where buyer_id = #{buyerId} order by create_time desc")
    List<OrderInfo> findByBuyer(Long buyerId);

    // 最近购买的商品ID（仅支付成功）
    @Select("select product_id from order_info where buyer_id = #{userId} and pay_status = 1 order by create_time desc limit #{limit}")
    List<Long> findRecentProductIdsByBuyer(Long userId, Integer limit);

    // 新增：根据订单ID查询订单（支付接口用）
    @Select("select * from order_info where id = #{id}")
    OrderInfo findById(Long id);

    // 新增：根据订单号查询订单（回调接口方法）
    @Select("select * from order_info where order_no = #{orderNo}")
    OrderInfo selectByOrderNo(String orderNo);


    @Update("update order_info set order_status = 1, pay_status = 1, pay_time = now() where order_no = #{orderNo}")
    void updatePaySuccess(String orderNo);

    // 买家订单列表（按buyer_id查询）
    @Select("""
    SELECT
        o.id,
        o.order_no,
        o.total_price,
        o.order_status,
        o.pay_status,
        o.pay_time,
        o.create_time,
        p.id AS product_id,
        p.title AS product_title,
        p.cover_img,
        u.id AS seller_id,
        u.username AS seller_name,
        u.avatar AS seller_avatar
    FROM order_info o
    LEFT JOIN product p ON o.product_id = p.id
    LEFT JOIN user u ON o.seller_id = u.id
    WHERE o.buyer_id = #{userId}
    ORDER BY o.create_time DESC
""")
    List<BuyerOrderListVO> listBuyerOrders(Long userId);

    // 卖家订单列表（按seller_id查询）
    @Select("""
SELECT
    o.id,
    o.order_no,
    o.total_price,
    o.order_status,
    o.pay_status,
    o.pay_time,
    o.create_time,
    p.id AS product_id,
    p.title AS product_title,
    p.cover_img,
    u.id AS buyer_id,
    u.username AS buyer_name,
    u.avatar AS buyer_avatar
FROM order_info o
LEFT JOIN product p ON o.product_id = p.id
LEFT JOIN user u ON o.buyer_id = u.id
WHERE o.seller_id = #{userId}
ORDER BY o.create_time DESC
""")
// 核心修改：返回类型改为 SellerOrderListVO
    List<SellerOrderListVO> listSellerOrders(Long userId);

    // 查询所有订单（管理员用）
    @Select("select * from order_info order by create_time desc")
    List<OrderInfo> findAllForAdmin();

    // 更新订单状态（管理员用）
    @Update("update order_info set order_status = #{orderStatus} where id = #{id}")
    void updateOrderStatus(Long id, Integer orderStatus);
}