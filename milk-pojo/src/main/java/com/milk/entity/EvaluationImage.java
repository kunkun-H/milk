package com.milk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationImage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer evaluationId;
    private String image;
    private LocalDateTime createTime;
}
