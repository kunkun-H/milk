package com.milk.service.impl;

import com.milk.constant.ParamsConstant;
import com.milk.entity.SeckillVoucher;
import com.milk.mapper.VoucherMapper;
import com.milk.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName: VoucherServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 11:08
 * @Version 1.0
 */
@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VoucherMapper voucherMapper;


    @Override
    @Transactional
    public void addSeckillVoucher(SeckillVoucher voucher) {
        // 保存秒杀优惠券信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        voucherMapper.insert(seckillVoucher);
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue().set(ParamsConstant.SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
    }



}
