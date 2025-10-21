package com.milk.service;

import com.milk.dto.*;
import com.milk.result.PageResult;
import com.milk.vo.OrderPaymentVO;
import com.milk.vo.OrderStatisticsVO;
import com.milk.vo.OrderSubmitVO;
import com.milk.vo.OrderVO;

/**
 * ClassName: OrderService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 23:34
 * @Version 1.0
 */
public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo,Integer payMethod);

    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO details(Long id);

    void cancelOrder(Long id) throws Exception;

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    void delivery(Long id);

    void complete(Long id);

    void reminder(Long id);


}
