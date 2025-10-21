package com.milk.mapper;

import com.milk.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ClassName: ShoppingCartMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 17:31
 * @Version 1.0
 */
@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number =#{number} where id=#{id}")
    void updateNumberById(ShoppingCart cart);


    void insert(ShoppingCart shoppingCart);


    @Delete("delete from shopping_cart where user_id = #{userId}")
    void delete(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    void insertBatch(List<ShoppingCart> list);
}
