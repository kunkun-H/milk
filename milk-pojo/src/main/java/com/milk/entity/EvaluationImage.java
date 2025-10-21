package com.milk.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: EvaluationImages
 * Package: com.milk.entity
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/1 22:55
 * @Version 1.0
 */
@Data
public class EvaluationImage {
    private Integer id;
    private Integer evaluationId;
    private String image;
    private LocalDateTime createTime;
}
