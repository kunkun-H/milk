package com.milk.mapper;

import com.milk.entity.SetmealGoods;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: SetmealGoodsMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/4 1:23
 * @Version 1.0
 */
@Mapper
public interface SetmealGoodsMapper {
    /**
     * 根据商品id查询对应的套餐id
     * @param goodsIds
     * @return
     */
    List<Long> getSetmealIdsByGoodsIds(List<Long> goodsIds);

    void insertBatch(List<SetmealGoods> setmealGoods);

    @Delete("delete from setmeal_goods where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("select * from setmeal_goods where setmeal_id = #{setmealId}")
    List<SetmealGoods> getBySetmealId(Long id);

}
