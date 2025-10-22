package com.milk.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * ClassName: RedisIdWorker
 * Package: com.milk.utils
 * Description:利用Redis实现全局唯一id
 *
 * @Author 何坤燃
 * @Create 2025/10/22 10:45
 * @Version 1.0
 */
@Component
public class RedisIdWorkerUtil {
    // 起始时间戳（2025-01-01 00:00:00, UTC+8）
    private static final long BEGIN_TIMESTAMP =
            LocalDateTime.of(2025, 1, 1, 0, 0, 0)
                    .toEpochSecond(ZoneOffset.of("+8"));
    /**
     * 序列号位数
     */
    private static final int COUNT_BITS = 32;

    private StringRedisTemplate stringRedisTemplate;

    public RedisIdWorkerUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long nextId(String keyPrefix) {
        // 1. 生成时间戳（当前秒 - 起始秒）
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.of("+8"));
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2. 生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        String key = String.format("incr:%s:%s", keyPrefix, date);
        long count = stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, Duration.ofDays(2));

        // 3. 拼接返回
        return (timestamp << COUNT_BITS) | count;
    }

}
