package com.milk.constant;

public class RabbitMQConstants {
    // 正常队列
    public static final String EXCHANGE_VOUCHER_ORDER = "voucher.order.exchange";
    public static final String QUEUE_VOUCHER_ORDER = "voucher.order.queue";
    public static final String ROUTING_KEY_VOUCHER_ORDER = "voucher.order.key";

    // 死信队列
    public static final String DEAD_EXCHANGE = "voucher.order.dead.exchange";
    public static final String DEAD_QUEUE = "voucher.order.dead.queue";
    public static final String DEAD_ROUTING_KEY = "voucher.order.dead.key";
}
