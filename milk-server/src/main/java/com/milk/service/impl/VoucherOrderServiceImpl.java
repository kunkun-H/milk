package com.milk.service.impl;

import com.milk.constant.RabbitMQConstants;
import com.milk.context.BaseContext;
import com.milk.entity.SeckillVoucher;
import com.milk.entity.VoucherOrder;
import com.milk.mapper.VoucherMapper;
import com.milk.mapper.VoucherOrderMapper;
import com.milk.result.Result;
import com.milk.service.VoucherOrderService;
import com.milk.utils.RedisIdWorkerUtil;
import com.milk.utils.RedisLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
@Slf4j
public class VoucherOrderServiceImpl implements VoucherOrderService {
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private VoucherOrderMapper voucherOrderMapper;
    @Autowired
    private RedisIdWorkerUtil redisIdWorkerUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
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

    /**
     * 秒杀优化：利用mq异步下单
     * @param voucherId
     * @return
     */
    public Result seckillVoucher1(Long voucherId) {
        // 获取用户ID
        Long userId = BaseContext.getCurrentId();
        // 生成全局唯一订单ID
        long orderId = redisIdWorkerUtil.nextId("order");

        // 1. 执行 Lua 脚本（判断库存、是否重复下单）
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );

        int r = result.intValue();
        if (r != 0) {
            return Result.error(r == 1 ? "库存不足" : "不能重复下单");
        }

        // 2. 构造消息体
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", userId);
        msg.put("voucherId", voucherId);
        msg.put("orderId", orderId);

        // 3. 发送消息到 MQ
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.EXCHANGE_VOUCHER_ORDER,
                    RabbitMQConstants.ROUTING_KEY_VOUCHER_ORDER,
                    msg,
                    message -> {
                        // 设置消息ID，用于幂等追踪
                        message.getMessageProperties().setMessageId(String.valueOf(orderId));
                        return message;
                    }
            );
            log.info("发送订单消息成功：orderId={}, userId={}, voucherId={}", orderId, userId, voucherId);
        } catch (Exception e) {
            log.error("发送订单消息失败：{}", e.getMessage(), e);
            stringRedisTemplate.opsForValue().increment("seckill_voucher:stock:" + voucherId);
            return Result.error("下单失败，请稍后重试");
        }
        // 4. 返回订单号
        return Result.success(orderId);
    }

    @PostConstruct
    public void initConfirmCallback() {
        // 确认消息是否成功投递到交换机
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息投递到交换机失败：{}", cause);
            }
        });
    }
}
