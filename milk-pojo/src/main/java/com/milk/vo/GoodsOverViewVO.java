package com.milk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品总览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsOverViewVO implements Serializable {
    // 已启售数量
    private Integer sold;

    // 已停售数量
    private Integer discontinued;
}
