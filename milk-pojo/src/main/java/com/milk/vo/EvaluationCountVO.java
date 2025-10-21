package com.milk.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCountVO  implements Serializable {
    private int allCount;
    private int goodCount;
    private int badCount;
    private int hasImageCount;
}
