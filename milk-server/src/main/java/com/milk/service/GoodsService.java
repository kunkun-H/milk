package com.milk.service;

import com.milk.dto.GoodsDTO;
import com.milk.dto.GoodsPageQueryDTO;
import com.milk.entity.Goods;
import com.milk.result.PageResult;
import com.milk.vo.GoodsVO;

import java.util.List;

/**
 * ClassName: GoodsService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/2 23:44
 * @Version 1.0
 */
public interface GoodsService {

    void saveWithFlavor(GoodsDTO goodsDTO);

    PageResult page(GoodsPageQueryDTO goodsPageQueryDTO);

    void openOrStop(Integer status, Long id);

    void update(GoodsDTO goodsDTO);

    GoodsVO selectById(Long id);

    void deleteBatch(List<Long> ids);


    List<Goods> list(Long categoryId);

    /**
     * 条件查询商品和口味
     * @param goods
     * @return
     */
    List<GoodsVO> listWithFlavor(Goods goods);

    List<Goods> getListByName(String name);
}
