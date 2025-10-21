package com.milk.service.impl;

import com.github.pagehelper.PageHelper;
import com.milk.constant.MessageConstant;
import com.milk.constant.StatusConstant;
import com.milk.context.BaseContext;
import com.milk.dto.CategoryDTO;
import com.milk.dto.CategoryPageQueryDTO;
import com.milk.entity.Category;
import com.milk.exception.DeletionNotAllowedException;
import com.milk.mapper.CategoryMapper;
import com.milk.mapper.GoodsMapper;
import com.milk.mapper.SetmealMapper;
import com.milk.result.PageResult;
import com.milk.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: CategoryServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/1/31 23:51
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page=categoryMapper.page(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void insert(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    @Override
    public void openOrStop(Integer status, Long id) {
        Category category=new Category();
        category.setId(id);
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    @Override
    public List<Category> selectByType(Integer type) {
        return categoryMapper.list(type);
    }

    @Override
    public void deleteById(Long id) {
        Category category=categoryMapper.getById(id);
        //查询当前类型的状态是否是启用，启用不能删除
        if(category!=null && category.getStatus()==StatusConstant.ENABLE){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_ON_SALE);
        }
        //查询当前分类是否关联了商品，如果关联了就抛出业务异常
        Integer count = goodsMapper.countByCategoryId(id);
        if(count!=null && count > 0){
            //当前分类下有商品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count!=null && count > 0){
            //当前分类下有商品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteById(id);
    }
}
