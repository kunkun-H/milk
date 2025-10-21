package com.milk.mapper;

import com.github.pagehelper.Page;
import com.milk.annotation.AutoFill;
import com.milk.dto.GoodsPageQueryDTO;
import com.milk.entity.Goods;
import com.milk.enumeration.OperationType;
import com.milk.vo.GoodsVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {

    /**
     * 根据分类id查询商品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from goods where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Goods goods);

    Page<GoodsVO> page(GoodsPageQueryDTO goodsPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Goods goods);


    Goods getById(Long id);

    @Delete("delete from goods where id = #{id}")
    void deleteById(Long id);

    void deleteBatchByIds(List<Long> ids);


    List<Goods> getListByCategoryId(Goods goods);

    @Select("select a.* from goods a left join setmeal_goods b on a.id = b.goods_id where b.setmeal_id = #{setmealId}")
    List<Goods> getBySetmealId(Long id);

    /**
     * 根据条件统计商品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    @Select("select * from goods where name=#{name}")
    Goods getByName(String name);
}
