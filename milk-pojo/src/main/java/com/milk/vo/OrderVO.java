package com.milk.vo;

import com.milk.entity.OrderDetail;
import com.milk.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单商品信息
    private String orderGoods;

    //订单详情
    private List<OrderDetail> orderDetailList;

}
