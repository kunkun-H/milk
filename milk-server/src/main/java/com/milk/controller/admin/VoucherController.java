package com.milk.controller.admin;

import com.milk.entity.SeckillVoucher;
import com.milk.result.Result;
import com.milk.service.VoucherService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: VoucherController
 * Package: com.milk.controller.admin
 * Description:优惠券管理
 *
 * @Author 何坤燃
 * @Create 2025/10/22 11:03
 * @Version 1.0
 */
@RestController("adminVoucherController")
@RequestMapping("/admin/voucher")
@Api(value = "VoucherController", description = "优惠券管理")
@Slf4j
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    /**
     * 添加秒杀优惠券
     * @param seckillVoucher
     * @return
     */
    @PostMapping("/seckill")
    public Result addSeckillVoucher(@RequestBody SeckillVoucher seckillVoucher) {
        voucherService.addSeckillVoucher(seckillVoucher);
        return Result.success(seckillVoucher.getId());
    }
}
