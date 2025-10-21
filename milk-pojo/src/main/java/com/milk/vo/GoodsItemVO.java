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
public class GoodsItemVO implements Serializable {

    //商品名称
    private String name;

    //份数
    private Integer copies;

    //商品图片
    private String image;

    //商品描述
    private String description;
}
