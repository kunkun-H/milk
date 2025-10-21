package com.milk.controller.admin;

import com.milk.dto.OrdersCancelDTO;
import com.milk.dto.OrdersConfirmDTO;
import com.milk.dto.OrdersPageQueryDTO;
import com.milk.dto.OrdersRejectionDTO;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.OrderService;
import com.milk.vo.OrderStatisticsVO;
import com.milk.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: OrderController
 * Package: com.milk.controller.admin
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/11 17:33
 * @Version 1.0
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(value = "OrderController", description = "订单管理")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索 {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("查看订单详情 {}", id);
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @GetMapping("/statistics")
    @ApiOperation("查询各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        log.info("查询各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long id) {
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }

}
