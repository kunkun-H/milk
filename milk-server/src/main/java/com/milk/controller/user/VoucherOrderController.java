package com.milk.controller.user;

import com.milk.result.Result;
import com.milk.service.VoucherOrderService;
import com.milk.service.VoucherService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: VoucherController
 * Package: com.milk.controller.user
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 13:58
 * @Version 1.0
 */
@RestController("userVoucherOrderController")
@RequestMapping("/user/voucher")
@Api(value = "VoucherOrderController", description = "秒杀优惠券")
@Slf4j
public class VoucherOrderController {
    @Autowired
    private VoucherOrderService voucherOrderService;
    @PostMapping("/seckill/{voucherId}")
    public Result seckillVoucher(@PathVariable("voucherId") Long voucherId){
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
