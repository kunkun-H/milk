package com.milk.controller.admin;

import com.milk.dto.CategoryDTO;
import com.milk.dto.CategoryPageQueryDTO;
import com.milk.entity.Category;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CategoryController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/1/31 23:48
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询 {}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("添加分类")
    public Result insert(@RequestBody CategoryDTO categoryDTO){
        log.info("添加分类 {}", categoryDTO);
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类 {}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("开启或关闭分类")
    public Result openOrStop(@PathVariable Integer status,Long id){
        log.info("开启或关闭分类 {} {}",status,id);
        categoryService.openOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> selectByType(Integer type){
        log.info("根据类型查询分类 {}", type);
        List<Category> category = categoryService.selectByType(type);
        return Result.success(category);
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result deleteById(Long id){
        log.info("删除分类 {}", id);
        categoryService.deleteById(id);
        return Result.success();
    }
}
