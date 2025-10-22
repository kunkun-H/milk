package com.milk.service.impl;

import com.milk.context.BaseContext;
import com.milk.entity.SeckillVoucher;
import com.milk.entity.VoucherOrder;
import com.milk.mapper.VoucherMapper;
import com.milk.mapper.VoucherOrderMapper;
import com.milk.result.Result;
import com.milk.service.VoucherOrderService;
import com.milk.utils.RedisIdWorkerUtil;
import com.milk.utils.RedisLockUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ClassName: VoucherOrderImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/10/22 14:29
 * @Version 1.0
 */
@Service
public class VoucherOrderServiceImpl implements VoucherOrderService {
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private VoucherOrderMapper voucherOrderMapper;
    @Autowired
    private RedisIdWorkerUtil redisIdWorkerUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result seckillVoucher(Long voucherId) {
        // 1.查询优惠券
        SeckillVoucher voucher = voucherMapper.getById(voucherId);
        // 2.判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.error("秒杀尚未开始！");
        }
        // 3.判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未开始
            return Result.error("秒杀已经结束！");
        }
        // 4.判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.error("库存不足！");
        }
        Long userId = BaseContext.getCurrentId();
        RedisLockUtil lock = new RedisLockUtil("order:" + userId, stringRedisTemplate);
        //获取锁对象
        boolean isLock = lock.tryLock(1200);
        //加锁失败
        if (!isLock) {
            return Result.error("不允许重复下单");
        }
        try {
            //获取代理对象(事务)
            VoucherOrderService proxy = (VoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    @Transactional
    public  Result createVoucherOrder(Long voucherId) {
        Long userId = BaseContext.getCurrentId();
        // 5.1.查询订单
        int count = voucherOrderMapper.countByUserAndVoucher(userId,voucherId);
        // 5.2.判断是否存在
        if (count > 0) {
            // 用户已经购买过了
            return Result.error("用户已经购买过一次！");
        }

        // 6.扣减库存
        boolean success = voucherMapper.update(voucherId)>0;
        if (!success) {
            // 扣减失败
            return Result.error("库存不足！");
        }

        // 7.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 7.1.订单id
        long orderId = redisIdWorkerUtil.nextId("order");
        voucherOrder.setId(orderId);
        // 7.2.用户id
        voucherOrder.setUserId(userId);
        // 7.3.代金券id
        voucherOrder.setVoucherId(voucherId);
        voucherOrderMapper.insert(voucherOrder);
        // 7.返回订单id
        return Result.success(orderId);

    }
}
