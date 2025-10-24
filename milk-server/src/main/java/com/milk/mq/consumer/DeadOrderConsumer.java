package com.milk.mq.consumer;

import com.milk.constant.ParamsConstant;
import com.milk.constant.RabbitMQConstants;
import com.milk.mapper.VoucherOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class DeadOrderConsumer {

    @Autowired
    private VoucherOrderMapper voucherOrderMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(queues = RabbitMQConstants.DEAD_QUEUE)
    public void handleDeadMessage(Map<String, Object> msg, Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        Long userId = Long.valueOf(msg.get("userId").toString());
        Long voucherId = Long.valueOf(msg.get("voucherId").toString());
        Long orderId = Long.valueOf(msg.get("orderId").toString());

        try {
            // 1. 确认数据库中订单是否创建
            int count = voucherOrderMapper.countByUserAndVoucher(userId, voucherId);
            if (count == 0) {
                // 2. 数据库中没有订单，则回补库存
                stringRedisTemplate.opsForValue().increment(ParamsConstant.SECKILL_STOCK_KEY + voucherId);
                log.warn("【死信补偿】库存已回补 voucherId={}, userId={}", voucherId, userId);
            } else {
                log.info("死信消息检测：订单已存在，无需回补库存 orderId={}", orderId);
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理死信消息失败：orderId={}, error={}", orderId, e.getMessage(), e);
            channel.basicNack(tag, false, false);
        }
    }
}
