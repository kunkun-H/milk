package com.milk.mapper;

import com.github.pagehelper.Page;
import com.milk.annotation.AutoFill;
import com.milk.dto.CategoryPageQueryDTO;
import com.milk.entity.Category;
import com.milk.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: CategoryMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/1/31 23:52
 * @Version 1.0
 */
@Mapper
public interface CategoryMapper {


    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(value= OperationType.INSERT)
    void insert(Category category);

    @AutoFill(value= OperationType.UPDATE)
    void update(Category category);


    List<Category> list(Integer type);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    @Select("select * from category where id=#{id}")
    Category getById(Long id);
}
