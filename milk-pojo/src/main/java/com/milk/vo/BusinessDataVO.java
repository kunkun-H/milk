package com.milk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO implements Serializable {

    private Double turnover;//营业额

    private Integer validOrderCount;//有效订单数

    private Double orderCompletionRate;//订单完成率

    private Double unitPrice;//平均客单价

    private Integer newUsers;//新增用户数
    //评价总数
    private Integer totalEvaluationCount;
    //好评
    private Integer goodEvaluationCount;
    //差评
    private Integer badEvaluationCount;
    //订单好评率
    private Double orderGoodRate;


}
