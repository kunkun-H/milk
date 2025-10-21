package com.milk.controller.user;

import com.milk.constant.StatusConstant;
import com.milk.entity.Setmeal;
import com.milk.result.Result;
import com.milk.service.SetmealService;
import com.milk.vo.GoodsItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "C端-套餐浏览接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames="setmealCache" ,key="#categoryId")
    public Result<List<Setmeal>> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的商品列表
     *
     * @param id
     * @return
     */
    @GetMapping("/goods/{id}")
    @ApiOperation("根据套餐id查询包含的商品列表")
    public Result<List<GoodsItemVO>> goodsList(@PathVariable("id") Long id) {
        List<GoodsItemVO> list = setmealService.getGoodsItemById(id);
        return Result.success(list);
    }
}
