package com.milk.mapper;

import com.milk.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id=#{userId}")
    ShoppingCart selectByUserId(Long userId);
}
