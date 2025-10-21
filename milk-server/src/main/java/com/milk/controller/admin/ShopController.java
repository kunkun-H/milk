package com.milk.controller.admin;

import com.milk.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: ShopController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/6 20:27
 * @Version 1.0
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(value = "ShopController", description = "门店管理")
@Slf4j
public class ShopController {
    public static final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态 {}", status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer status =(Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态 {}", status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
