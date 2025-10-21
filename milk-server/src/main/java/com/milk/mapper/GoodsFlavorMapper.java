package com.milk.mapper;

import com.milk.entity.GoodsFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: GoodsFlavorMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/3 18:06
 * @Version 1.0
 */
@Mapper
public interface GoodsFlavorMapper {
    void insertBatch(List<GoodsFlavor> flavors);

    @Select("select * from goods_flavor where goods_id=#{id}")
    List<GoodsFlavor> getByGoodsId(Long id);



    void deleteByGoodsId(Long goodsId);

    void deleteBatchByGoodsIds(List<Long> ids);
}
