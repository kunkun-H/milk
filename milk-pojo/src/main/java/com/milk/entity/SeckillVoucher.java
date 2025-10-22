package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: SeckillVoucher
 * Package: com.milk.entity
 * Description:秒杀优惠券表
 *
 * @Author 何坤燃
 * @Create 2025/10/22 10:36
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillVoucher implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer stock;//库存
    private LocalDateTime createTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime updateTime;
}
