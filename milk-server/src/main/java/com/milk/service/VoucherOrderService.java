package com.milk.service;

import com.milk.result.Result;

/**
 * ClassName: VoucherOrder
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 14:29
 * @Version 1.0
 */
public interface VoucherOrderService {
    Result seckillVoucher(Long voucherId);

    Result createVoucherOrder(Long voucherId);
}
