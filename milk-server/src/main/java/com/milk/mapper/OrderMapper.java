package com.milk.mapper;

import com.github.pagehelper.Page;
import com.milk.dto.GoodsSalesDTO;
import com.milk.dto.OrdersPageQueryDTO;
import com.milk.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ClassName: OrderMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 23:50
 * @Version 1.0
 */
@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);


    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")

    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime time);

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select * from orders where id=#{orderId}")
    Orders selectById(Long orderId);
}
