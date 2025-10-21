package com.milk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationReportVO implements Serializable {

    //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
    private String dateList;

    //每日好评订单数，以逗号分隔，例如：260,210,215
    private String goodOrderCountList;

    //每日差评订单数，以逗号分隔，例如：20,21,10
    private String badOrderCountList;
    //每日订单评价数
    private String orderCountList;

    //评价总数
    private Integer totalOrderCount;
    //好评
    private Integer goodOrderCount;
    //差评
    private Integer badOrderCount;
    //订单好评率
    private Double orderGoodRate;

}
