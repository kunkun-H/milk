package com.milk.controller.user;

import com.milk.dto.OrdersPageQueryDTO;
import com.milk.dto.OrdersPaymentDTO;
import com.milk.dto.OrdersSubmitDTO;
import com.milk.result.PageResult;
import com.milk.result.Result;
import com.milk.service.OrderService;
import com.milk.vo.OrderPaymentVO;
import com.milk.vo.OrderSubmitVO;
import com.milk.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: OrderController
 * Package: com.milk.controller.user
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 23:32
 * @Version 1.0
 */
@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(value = "OrderController", description = "用户订单管理")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("提交订单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询用户历史订单")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询用户历史订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.page(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/evaluatedOrders")
    @ApiOperation("查询用户已评价订单")
    public Result<PageResult> getEvaluatedOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询用户已评价订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.page(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        OrderVO orderVO=orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable Long id) throws Exception {
        log.info("取消订单：{}", id);
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id){
        log.info("用户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }
}
