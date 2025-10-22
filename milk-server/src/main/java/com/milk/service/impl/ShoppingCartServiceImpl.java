package com.milk.service.impl;

import com.milk.context.BaseContext;
import com.milk.dto.ShoppingCartDTO;
import com.milk.entity.CartItem;
import com.milk.entity.Goods;
import com.milk.entity.Setmeal;
import com.milk.entity.ShoppingCart;
import com.milk.mapper.CartItemMapper;
import com.milk.mapper.GoodsMapper;
import com.milk.mapper.SetmealMapper;
import com.milk.mapper.ShoppingCartMapper;
import com.milk.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: ShoppingCartServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 17:30
 * @Version 1.0
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart=shoppingCartMapper.selectByUserId(userId);
        if(cart==null){
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
        Long cartId= cart==null?shoppingCart.getId():cart.getId();
        CartItem cartItem=new CartItem();
        BeanUtils.copyProperties(shoppingCartDTO,cartItem);
        cartItem.setCartId(cartId);
        List<CartItem> cartList= cartItemMapper.list(cartItem);
        if(cartList!=null && cartList.size()>0){
            CartItem item = cartList.get(0);
            item.setNumber(item.getNumber()+1);
            cartItemMapper.updateNumberById(item);
        }else{
            Long goodsId = shoppingCartDTO.getGoodsId();
            if(goodsId!=null){
                Goods goods = goodsMapper.getById(goodsId);
                cartItem.setName(goods.getName());
                cartItem.setImage(goods.getImage());
                cartItem.setAmount(goods.getPrice());
            }else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                cartItem.setName(setmeal.getName());
                cartItem.setImage(setmeal.getImage());
                cartItem.setAmount(setmeal.getPrice());
            }
            cartItem.setNumber(1);
            cartItem.setCreateTime(LocalDateTime.now());

            cartItemMapper.insert(cartItem);
        }
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        CartItem cartItem=new CartItem();
        BeanUtils.copyProperties(shoppingCartDTO,cartItem);
        List<CartItem> list = cartItemMapper.list(cartItem);

        if(list != null && list.size() > 0){
            cartItem = list.get(0);

            Integer number = cartItem.getNumber();
            if(number == 1){
                //当前商品在购物车明细中的份数为1，直接删除当前记录
                cartItemMapper.deleteById(cartItem.getId());
            }else {
                //当前商品在购物车明细中的份数不为1，修改份数即可
                cartItem.setNumber(cartItem.getNumber() - 1);
                cartItemMapper.updateNumberById(cartItem);
            }
        }
    }

    @Override
    public List<CartItem> getShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = shoppingCartMapper.selectByUserId(userId);
        ShoppingCart cart=new ShoppingCart();
        if(shoppingCart==null){
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        }
        Long cartId=shoppingCart==null?cart.getId():shoppingCart.getId();
        CartItem cartItem=new CartItem();
        cartItem.setCartId(cartId);
        return cartItemMapper.list(cartItem);
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = shoppingCartMapper.selectByUserId(userId);
        Long cartId=shoppingCart.getId();
        cartItemMapper.delete(cartId);
    }
}
