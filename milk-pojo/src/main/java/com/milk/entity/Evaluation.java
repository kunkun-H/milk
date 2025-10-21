package com.milk.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: Evaluation
 * Package: com.sky.entity
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/1 22:52
 * @Version 1.0
 */
@Data
public class Evaluation {
    private Integer id;
    private Long userId;
    private Long orderId;
    //订单号
    private String number;
    //手机号
    private String consignee;//用户名
    private String phone;
    private String content;
    private Integer score;
    private Integer isReply;
    private LocalDateTime createTime;
}
