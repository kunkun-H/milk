package com.milk.controller.user;

import com.milk.constant.StatusConstant;
import com.milk.entity.Goods;
import com.milk.result.Result;
import com.milk.service.GoodsService;
import com.milk.vo.GoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userGoodsController")
@RequestMapping("/user/goods")
@Slf4j
@Api(tags = "C端-商品浏览接口")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询商品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询商品")
    public Result<List<GoodsVO>> list(Long categoryId) {
        String key="goods_"+categoryId;
        List<GoodsVO> list = (List<GoodsVO>) redisTemplate.opsForValue().get(key);
        if(list!=null && list.size()>0){
            return Result.success(list);
        }
        Goods goods = new Goods();
        goods.setCategoryId(categoryId);
        goods.setStatus(StatusConstant.ENABLE);//查询起售中的商品

        list = goodsService.listWithFlavor(goods);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }

}
