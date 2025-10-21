package com.milk.controller.user;

import com.milk.result.Result;
import com.milk.service.EvaluationService;
import com.milk.vo.EvaluationCountVO;
import com.milk.vo.EvaluationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: ShopController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/6 20:27
 * @Version 1.0
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(value = "ShopController", description = "门店管理")
@Slf4j
public class ShopController {
    public static final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态 {}", status==1?"营业中":"打烊中");
        return Result.success(status);
    }

    @GetMapping("/getMerchantInfo")
    public Result getMerchantInfo(){
        return Result.success();
    }

    @GetMapping("/getAllEvaluation")
    public Result<List<EvaluationVO>> getShopAllEvaluation(String type){
        List<EvaluationVO> evaluationVOList= evaluationService.getShopAllEvaluation(type);
        return Result.success(evaluationVOList);
    }
    @GetMapping("/getEvaluationCount")
    public Result<EvaluationCountVO> getEvaluationCount(){
        EvaluationCountVO evaluationCountVOList= evaluationService.getEvaluationCount();
        return Result.success(evaluationCountVOList);
    }
}
