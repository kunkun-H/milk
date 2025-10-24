package com.milk.mq.consumer;

import com.milk.constant.ParamsConstant;
import com.milk.constant.RabbitMQConstants;
import com.milk.entity.VoucherOrder;
import com.milk.mapper.VoucherMapper;
import com.milk.mapper.VoucherOrderMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class VoucherOrderConsumer {
    @Autowired
    private VoucherOrderMapper voucherOrderMapper;
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 消费秒杀订单消息
     */
    @Transactional
    @RabbitListener(queues = RabbitMQConstants.QUEUE_VOUCHER_ORDER)
    public void handleOrder(Map<String, Object> msg, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        Long userId = Long.valueOf(msg.get("userId").toString());
        Long voucherId = Long.valueOf(msg.get("voucherId").toString());
        Long orderId = Long.valueOf(msg.get("orderId").toString());

        try {
            log.info("开始处理订单：orderId={}, userId={}, voucherId={}", orderId, userId, voucherId);

            // 1. 幂等性检查（是否已经下单）
            int count = voucherOrderMapper.countByUserAndVoucher(userId, voucherId);
            if (count > 0) {
                log.warn("用户 {} 已购买过优惠券 {}，忽略重复消息", userId, voucherId);
                stringRedisTemplate.opsForValue().increment(ParamsConstant.SECKILL_STOCK_KEY + voucherId);
                channel.basicAck(tag, false);
                return;
            }

            // 2. 扣减库存（仅当库存 > 0）
            boolean success = voucherMapper.update(voucherId) > 0;
            if (!success) {
                log.warn("库存扣减失败，voucherId={}", voucherId);
                stringRedisTemplate.opsForValue().increment(ParamsConstant.SECKILL_STOCK_KEY + voucherId);
                channel.basicAck(tag, false);
                return;
            }

            // 3. 创建订单
            VoucherOrder order = new VoucherOrder();
            order.setId(orderId);
            order.setUserId(userId);
            order.setVoucherId(voucherId);
            voucherOrderMapper.insert(order);

            log.info("订单创建成功：orderId={}", orderId);

            // 4. 通知mq删除消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("订单创建失败：orderId={}, error={}", orderId, e.getMessage(), e);
            // 直接拒绝，不再入队，让消息进入死信队列
            channel.basicNack(tag, false, false);
        }
    }
}
