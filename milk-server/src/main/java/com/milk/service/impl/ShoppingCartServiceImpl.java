package com.milk.service.impl;

import com.milk.context.BaseContext;
import com.milk.dto.ShoppingCartDTO;
import com.milk.entity.Goods;
import com.milk.entity.Setmeal;
import com.milk.entity.ShoppingCart;
import com.milk.mapper.GoodsMapper;
import com.milk.mapper.SetmealMapper;
import com.milk.mapper.ShoppingCartMapper;
import com.milk.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> cartList= shoppingCartMapper.list(shoppingCart);
        if(cartList!=null && cartList.size()>0){
            ShoppingCart cart = cartList.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        }else{
            Long goodsId = shoppingCartDTO.getGoodsId();
            if(goodsId!=null){
                Goods goods = goodsMapper.getById(goodsId);
                shoppingCart.setName(goods.getName());
                shoppingCart.setImage(goods.getImage());
                shoppingCart.setAmount(goods.getPrice());
            }else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }
    }

    @Override
    public List<ShoppingCart> getShoopingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.delete(userId);
    }
}
