package com.milk.mapper;

import com.milk.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: OrderDetailMapper
 * Package: com.milk.mapper
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 23:50
 * @Version 1.0
 */
@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetails);

    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
