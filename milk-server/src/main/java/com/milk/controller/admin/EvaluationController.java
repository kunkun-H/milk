package com.milk.controller.admin;

import com.milk.dto.EvaluationPageQueryDTO;
import com.milk.dto.EvaluationReplyDTO;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.EvaluationService;
import io.swagger.annotations.ApiOperation;
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
@RestController("adminEvaluationController")
@Slf4j
@RequestMapping("/admin/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;
    @GetMapping("/conditionSearch")
    @ApiOperation("用户评价搜索")
    public Result<PageResult> conditionSearch(EvaluationPageQueryDTO evaluationPageQueryDTO){
        log.info("用户评价搜索 {}", evaluationPageQueryDTO);
        PageResult pageResult = evaluationService.conditionSearch(evaluationPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/reply")
    @ApiOperation("回复评价")
    public Result reply(EvaluationReplyDTO evaluationReplyDTO){
        evaluationService.reply(evaluationReplyDTO);
        return Result.success("回复成功");
    }

}
