package com.milk.service;

import com.milk.dto.CategoryDTO;
import com.milk.dto.CategoryPageQueryDTO;
import com.milk.entity.Category;
import com.milk.result.PageResult;

import java.util.List;

/**
 * ClassName: CategoryService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/1/31 23:50
 * @Version 1.0
 */
public interface CategoryService {
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void insert(CategoryDTO categoryDTO);

    void openOrStop(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    List<Category> selectByType(Integer type);

    void deleteById(Long id);
}
