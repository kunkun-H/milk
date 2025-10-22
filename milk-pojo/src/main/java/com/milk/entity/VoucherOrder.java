package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: VoucherOrder
 * Package: com.milk.entity
 * Description:优惠卷订单表
 *
 * @Author 何坤燃
 * @Create 2025/10/22 10:20
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;//用户id
    private Long voucherId;//优惠券id
    private Integer payType;//支付方式
    private Integer status;//订单状态
    private LocalDateTime createTime;
    private LocalDateTime payTime;//下单时间
    private LocalDateTime useTime;//核销时间
    private LocalDateTime refundTime;//退款时间
    private LocalDateTime updateTime;//更新时间
}
