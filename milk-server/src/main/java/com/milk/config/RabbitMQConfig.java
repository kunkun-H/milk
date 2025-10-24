package com.milk.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.milk.constant.RabbitMQConstants.*;
@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange voucherOrderExchange() {
        return new DirectExchange(EXCHANGE_VOUCHER_ORDER);
    }

    @Bean
    public Queue voucherOrderQueue() {
        Map<String, Object> args = new HashMap<>();
        // 当消息被拒绝或超时，会进入死信队列
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);
        return new Queue(QUEUE_VOUCHER_ORDER, true, false, false, args);
    }

    @Bean
    public Binding bindingOrderQueue() {
        return BindingBuilder.bind(voucherOrderQueue())
                .to(voucherOrderExchange())
                .with(ROUTING_KEY_VOUCHER_ORDER);
    }

    // 死信队列配置
    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_QUEUE, true);
    }

    @Bean
    public Binding bindingDeadQueue() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange())
                .with(DEAD_ROUTING_KEY);
    }

    // JSON 序列化
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}