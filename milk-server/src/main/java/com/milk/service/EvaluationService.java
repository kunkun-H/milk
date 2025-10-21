package com.milk.service;

import com.milk.dto.EvaluationDTO;
import com.milk.dto.EvaluationPageQueryDTO;
import com.milk.dto.EvaluationReplyDTO;
import com.milk.result.PageResult;
import com.milk.vo.EvaluationCountVO;
import com.milk.vo.EvaluationVO;

import java.util.List;

/**
 * ClassName: EvaluationService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/1 23:00
 * @Version 1.0
 */
public interface EvaluationService {
    void saveEvaluationWithImages(EvaluationDTO evaluationDTO);

    PageResult conditionSearch(EvaluationPageQueryDTO evaluationPageQueryDTO);

    void reply(EvaluationReplyDTO evaluationReplyDTO);

    EvaluationVO getEvaluationDeatils(Long orderId);

    List<EvaluationVO> getShopAllEvaluation(String type);

    EvaluationCountVO getEvaluationCount();
}
