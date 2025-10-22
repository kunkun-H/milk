package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ClassName: CartItem
 * Package: com.milk.entity
 * Description:购物车明细表
 *
 * @Author 何坤燃
 * @Create 2025/10/21 20:24
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long id;
    private Long cartId;
    private Long goodsId;
    private Long setmealId;
    private String goodsFlavor;
    private Integer number;
    private LocalDateTime createTime;
    private String name;//商品名称（冗余字段）
    private String image;//图片（冗余字段）
    private BigDecimal amount;//金额（冗余字段）
}
