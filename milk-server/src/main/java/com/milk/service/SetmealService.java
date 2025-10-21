package com.milk.service;

import com.milk.dto.SetmealDTO;
import com.milk.dto.SetmealPageQueryDTO;
import com.milk.entity.Setmeal;
import com.milk.result.PageResult;
import com.milk.vo.GoodsItemVO;
import com.milk.vo.SetmealVO;

import java.util.List;

/**
 * ClassName: SetmealService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/4 18:19
 * @Version 1.0
 */
public interface SetmealService {
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void save(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);

    void deleteBatch(List<Long> ids);

    SetmealVO getById(Long id);

    void update(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询商品选项
     * @param id
     * @return
     */
    List<GoodsItemVO> getGoodsItemById(Long id);
}
