package com.milk.controller.user;

import com.milk.dto.EvaluationDTO;
import com.milk.result.Result;
import com.milk.service.EvaluationService;
import com.milk.vo.EvaluationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: EvaluationController
 * Package: com.milk.controller.user
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/8/1 22:57
 * @Version 1.0
 */
@RestController("userEvaluationController")
@Slf4j
@RequestMapping("/user/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/submit")
    public Result submitEvaluation(@RequestBody EvaluationDTO evaluationDTO) {
        evaluationService.saveEvaluationWithImages(evaluationDTO);
        return Result.success("评价成功");
    }
    @GetMapping("/details/{orderId}")
    public Result getEvaluationDetails(@PathVariable Long orderId){
        EvaluationVO evaluationVO= evaluationService.getEvaluationDeatils(orderId);
        return Result.success(evaluationVO);
    }
}
