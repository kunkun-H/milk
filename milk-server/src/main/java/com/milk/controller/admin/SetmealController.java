package com.milk.controller.admin;

import com.milk.dto.SetmealDTO;
import com.milk.dto.SetmealPageQueryDTO;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.SetmealService;
import com.milk.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: SetmealController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/4 18:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(value = "SetmealController", description = "套餐管理")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询 {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


    @PostMapping
    @ApiOperation("添加套餐")
    @CacheEvict(cacheNames = "setmealCache",key="#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("添加套餐 {}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setmealService.startOrStop(status, id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除 {}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询 {}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐 {}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }
}
