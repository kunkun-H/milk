package com.milk.mapper;

import com.github.pagehelper.Page;
import com.milk.annotation.AutoFill;
import com.milk.dto.SetmealPageQueryDTO;
import com.milk.entity.Setmeal;
import com.milk.enumeration.OperationType;
import com.milk.vo.GoodsItemVO;
import com.milk.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param
     * @return
     */
    @Select("select * from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @Delete("delete from setmeal where id = #{setmealId}")
    void deleteById(Long setmealId);

    @AutoFill(value=OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询商品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_goods sd left join goods d on sd.goods_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<GoodsItemVO> getGoodsItemBySetmealId(Long setmealId);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    @Select("select * from setmeal where name=#{name}")
    Setmeal getByName(String name);

}
