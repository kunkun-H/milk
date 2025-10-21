package com.milk.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: EvaluationReply
 * Package: com.milk.entity
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/4 21:50
 * @Version 1.0
 */
@Data
public class EvaluationReply {
    private Integer id;
    private Integer evaluationId;
    private String replyContent;
    private Long adminId;
    private LocalDateTime replyTime;
}
