package com.milk.service;

import com.milk.vo.BusinessDataVO;
import com.milk.vo.GoodsOverViewVO;
import com.milk.vo.OrderOverViewVO;
import com.milk.vo.SetmealOverViewVO;
import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询商品总览
     * @return
     */
    GoodsOverViewVO getGoodsOverView();

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

}
