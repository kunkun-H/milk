package com.milk.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationDTO {
    private Long orderId;
    private String content;
    private Integer score;
    private List<String> imageUrls;
}
