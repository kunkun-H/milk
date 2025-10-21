package com.milk.vo;

import com.milk.entity.Evaluation;
import com.milk.entity.EvaluationImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationVO extends Evaluation implements Serializable {
    private String replyContent;
    private String adminUserName;
    private String avatar;
    //商品名称
    private String name;
    //评价的图片
    private List<EvaluationImage> evaluationImageList;


}
