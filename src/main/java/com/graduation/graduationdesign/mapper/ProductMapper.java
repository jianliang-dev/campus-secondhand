package com.graduation.graduationdesign.mapper;

import com.graduation.graduationdesign.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper（SQL字段与数据库对齐）
 */
@Mapper
public interface ProductMapper {

    // 发布商品
    @Insert("insert into product(user_id,title,description,price,category_id,cover_img,status,create_time) " +
            "values(#{userId},#{title},#{description},#{price},#{categoryId},#{coverImg},1,now())")
    void insert(Product product);

    // 商品列表（只显示上架）
    @Select("select * from product where status = 1 order by create_time desc")
    List<Product> findAll();

    // 商品详情
    @Select("select * from product where id = #{id}")
    Product findById(Long id);

    //新增：商品搜索方法（模糊查询标题，只查上架商品）
    @Select("select * from product " +
            "where status = 1 and title like concat('%',#{keyword},'%') " +
            "order by create_time desc")
    List<Product> search(String keyword);

    // 查询我的商品列表
    @Select("select * from product where user_id = #{userId} order by create_time desc")
    List<Product> findByUserId(Long userId);

    // 根据ID列表查询商品
    @Select("""
    <script>
    select * from product
    where id in
    <foreach collection="ids" item="id" open="(" separator="," close=")">
      #{id}
    </foreach>
    </script>
    """)
    List<Product> findByIds(@Param("ids") List<Long> ids);

    // 根据分类列表查询商品（只查上架）
    @Select("""
    <script>
    select * from product
    where status = 1
    and category_id in
    <foreach collection="categoryIds" item="cid" open="(" separator="," close=")">
      #{cid}
    </foreach>
    order by create_time desc
    limit #{limit}
    </script>
    """)
    List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds,
                                    @Param("limit") Integer limit);

    // 更新商品信息
    @Update("update product set title=#{title}, description=#{description}, price=#{price}, " +
            "category_id=#{categoryId}, cover_img=#{coverImg}, update_time=now() where id=#{id}")
    void update(Product product);

    // 删除商品
    @Delete("delete from product where id = #{id}")
    void delete(Long id);

    // 更新商品状态（上下架）
    @Update("update product set status=#{status}, update_time=now() where id=#{id}")
    void updateStatus(Long id, Integer status);

    // 查询所有商品（管理员用，包括下架商品）
    @Select("select * from product order by create_time desc")
    List<Product> findAllForAdmin();

}
