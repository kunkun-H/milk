package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品口味
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    //商品id
    private Long goodsId;

    //口味名称
    private String name;

    //口味数据list
    private String value;

}
