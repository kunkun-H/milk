package com.milk.mapper;

import com.milk.entity.CartItem;
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
public interface CartItemMapper {


    List<CartItem> list(CartItem cartItem);

    @Update("update cart_item set number =#{number} where id=#{id}")
    void updateNumberById(CartItem item);

    void insert(CartItem cartItem);

    @Delete("delete from cart_item where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from cart_item where cart_id = #{cartId}")
    void delete(Long cartId);

    void insertBatch(List<CartItem> list);
}
