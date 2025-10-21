package com.milk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EvaluationPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String number;

    private  String phone;

    private List<Integer> scoreList;

    private Long userId;

}
