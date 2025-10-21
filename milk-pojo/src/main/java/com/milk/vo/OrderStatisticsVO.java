package com.milk.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderStatisticsVO implements Serializable {
    // 待付款数量
    private Integer pendingPayment;
    //待接单数量
    private Integer toBeConfirmed;
    //制作中数量
    private Integer making;
    //待派送数量
    private Integer confirmed;

    //派送中数量
    private Integer deliveryInProgress;
}
