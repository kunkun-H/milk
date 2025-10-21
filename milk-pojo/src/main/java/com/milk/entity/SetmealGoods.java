package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐商品关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetmealGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    private Long setmealId;

    //商品id
    private Long goodsId;

    //商品名称 （冗余字段）
    private String name;

    //商品原价
    private BigDecimal price;

    //份数
    private Integer copies;
}
