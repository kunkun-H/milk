package com.milk.controller.admin;

import com.milk.dto.GoodsDTO;
import com.milk.dto.GoodsPageQueryDTO;
import com.milk.entity.Goods;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.GoodsService;
import com.milk.vo.GoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * ClassName: GoodsController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/2 23:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/goods")
@Slf4j
@Api(value = "GoodsController", description = "商品管理")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation(value = "添加商品")
    public Result insert(@RequestBody GoodsDTO goodsDTO) {
        log.info("添加商品：{}", goodsDTO);
        goodsService.saveWithFlavor(goodsDTO);
        String key="goods_"+ goodsDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(GoodsPageQueryDTO goodsPageQueryDTO){
        log.info("分页查询 {}", goodsPageQueryDTO);
        PageResult pageResult= goodsService.page(goodsPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售/停售")
    public Result openOrStop(@PathVariable Integer status,Long id){
        log.info("起售/停售 {} {}",status,id);
        goodsService.openOrStop(status,id);
        cleanCache("goods_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询商品")
    public Result<GoodsVO> selectById(@PathVariable Long id){
        log.info("根据id查询 {}", id);
        GoodsVO goodsVO = goodsService.selectById(id);
        return Result.success(goodsVO);
    }

    @PutMapping
    @ApiOperation("修改商品")
    public Result update(@RequestBody GoodsDTO goodsDTO){
        log.info("修改商品 {}", goodsDTO);
        goodsService.update(goodsDTO);
        cleanCache("goods_*");
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除商品")
    public Result delete(@RequestParam List<Long>ids){
        log.info("批量删除 {}", ids);
        goodsService.deleteBatch(ids);
        cleanCache("goods_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询商品")
    public Result<List<Goods>> list(Long categoryId){
        log.info("根据分类id查询 {}", categoryId);
        List<Goods> goods = goodsService.list(categoryId);
        return Result.success(goods);
    }
    @GetMapping("/getListByName")
    @ApiOperation("根据name查询商品")
    public Result<List<Goods>> getListByName(String name){
        log.info("根据name查询 {}", name);
        List<Goods> goods = goodsService.getListByName(name);
        return Result.success(goods);
    }

    /**
     * 清理缓存数据
     */
    public void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
