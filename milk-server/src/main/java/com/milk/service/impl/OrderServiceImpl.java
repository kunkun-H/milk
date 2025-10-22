package com.milk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.milk.constant.MessageConstant;
import com.milk.context.BaseContext;
import com.milk.dto.*;
import com.milk.entity.*;
import com.milk.exception.AddressBookBusinessException;
import com.milk.exception.OrderBusinessException;
import com.milk.exception.PhoneException;
import com.milk.exception.ShoppingCartBusinessException;
import com.milk.mapper.*;
import com.milk.result.PageResult;
import com.milk.service.OrderService;
import com.milk.utils.HttpClientUtil;
import com.milk.utils.WeChatPayUtil;
import com.milk.vo.OrderPaymentVO;
import com.milk.vo.OrderStatisticsVO;
import com.milk.vo.OrderSubmitVO;
import com.milk.vo.OrderVO;
import com.milk.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: OrderServiceImpl
 * Package: com.milk.service.impl
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/9 23:36
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealGoodsMapper setmealGoodsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Value("${milk.shop.address}")
    private String shopAddress;

    @Value("${milk.baidu.ak}")
    private String ak;
    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //判断地址簿的信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if("delivery".equals(ordersSubmitDTO.getDeliveryMethod())){
            if(addressBook==null){
                throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
            }
            checkOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        }

        //判断购物车是否有数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = shoppingCartMapper.selectByUserId(userId);
        CartItem cartItem=new CartItem();
        if(shoppingCart!=null){
            cartItem.setCartId(shoppingCart.getId());
        }
        List<CartItem> list = cartItemMapper.list(cartItem);
        if(list==null || list.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setPackAmount(1);//设置打包费为1块钱
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserId(userId);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        if("delivery".equals(ordersSubmitDTO.getDeliveryMethod())){
            orders.setPhone(addressBook.getPhone());
            orders.setConsignee(addressBook.getConsignee());
            orders.setAddress(addressBook.getDetail());
        }
        else{
            User user = userMapper.getById(userId);
            if(user.getPhone()==null || "".equals(user.getPhone())){
                throw new PhoneException("手机号为空，不能下单");
            }
            orders.setPhone(user.getPhone());
            orders.setConsignee(user.getName());
            orders.setAddress("");
        }
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setDeliveryMethod("delivery".equals(ordersSubmitDTO.getDeliveryMethod())?1:0);
        String takeNum="";
        do{
            int num = new Random().nextInt(10000); // 0 ~ 9999
            takeNum=String.format("%04d",num);
            if(redisTemplate.opsForValue().get("takeNumber"+takeNum)==null){
                redisTemplate.opsForValue().set("takeNumber"+takeNum,takeNum,2,TimeUnit.HOURS);
                break;
            }
        }while (true);
        orders.setTakeNumber(takeNum);
        orderMapper.insert(orders);

        List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
        for(CartItem cart:list){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        //清空购物车明细
        cartItemMapper.delete(shoppingCart.getId());

        OrderSubmitVO orderSubmitVO=new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        return orderSubmitVO;
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 20000){
            //配送距离超过20000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;


        paySuccess(ordersPaymentDTO.getOrderNumber(),ordersPaymentDTO.getPayMethod());
        return new OrderPaymentVO();

    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo,Integer payMethod) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .payMethod(payMethod)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        // 通过websocket向客户端浏览器推送消息 type orderId content
        Map map=new HashMap();
        map.put("type",1); // 1表示来单  2 表示客户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号"+outTradeNo);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentId();
        System.out.println(userId);
        ordersPageQueryDTO.setUserId(userId);
        Page<Orders> page=orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    @Override
    public OrderVO details(Long id) {
        Orders order=orderMapper.getById(id);
        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        List<OrderDetail> orderDetails=orderDetailMapper.getByOrderId(id);
        if(orderDetails!=null){
            for(OrderDetail orderDetail:orderDetails){
                if(orderDetail.getSetmealId()!=null){
                    List<SetmealGoods> setmealGoodsList = setmealGoodsMapper.getBySetmealId(orderDetail.getSetmealId());
                    // 拼接商品信息：商品名*数量，使用逗号分隔
                    StringBuilder goodsInfoBuilder = new StringBuilder();
                    for(SetmealGoods setmealGoods : setmealGoodsList){
                        goodsInfoBuilder
                                .append(setmealGoods.getName())
                                .append("*")
                                .append(setmealGoods.getCopies())
                                .append(", ");
                    }
                    // 去掉最后一个逗号和空格
                    if (goodsInfoBuilder.length() > 0) {
                        goodsInfoBuilder.delete(goodsInfoBuilder.length() - 2, goodsInfoBuilder.length());
                    }


                    // 将拼接好的字符串放入 OrderDetail
                    orderDetail.setGoodsInfo(goodsInfoBuilder.toString());

                }
            }
        }
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }



    @Override
    public void cancelOrder(Long id) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

//        // 订单处于待接单状态下取消，需要进行退款
//        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
//            //调用微信支付退款接口
//            weChatPayUtil.refund(
//                    ordersDB.getNumber(), //商户订单号
//                    ordersDB.getNumber(), //商户退款单号
//                    new BigDecimal(0.01),//退款金额，单位 元
//                    new BigDecimal(0.01));//原订单金额
//
//            //支付状态修改为 退款
//            orders.setPayStatus(Orders.REFUND);
//        }

        //支付状态修改为 退款
        if(ordersDB.getPayStatus()==1){
            orders.setPayStatus(Orders.REFUND);
        }else{
            orders.setPayStatus(Orders.UN_PAID);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        if(orderDetailList!=null && orderDetailList.size()>0){
            Long userId = BaseContext.getCurrentId();
            ShoppingCart shoppingCart = shoppingCartMapper.selectByUserId(userId);
            ShoppingCart cart=new ShoppingCart();
            if(shoppingCart==null){
                cart.setUserId(userId);
                cart.setCreateTime(LocalDateTime.now());
                shoppingCartMapper.insert(cart);
            }
            Long cartId=shoppingCart==null?cart.getId():shoppingCart.getId();
            List<CartItem> list=new ArrayList<>();
            for(OrderDetail o:orderDetailList){
                CartItem cartItem=new CartItem();
                BeanUtils.copyProperties(o,cartItem,"id");
                cartItem.setCartId(cartId);
                shoppingCart.setCreateTime(LocalDateTime.now());
                list.add(cartItem);
            }
            cartItemMapper.insertBatch(list);
        }
    }

    /**
     * 管理端
     */

    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态，需要额外返回订单商品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单商品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderGoods = getOrderGoodsStr(orders);

                // 将订单商品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderGoods(orderGoods);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取商品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderGoodsStr(Orders orders) {
        // 查询订单商品详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        List<String> orderGoodsList = new ArrayList<>();

        if (orderDetailList != null) {
            for (OrderDetail orderDetail : orderDetailList) {
                // 如果是套餐
                if (orderDetail.getSetmealId() != null) {
                    // 查询套餐下的商品
                    List<SetmealGoods> setmealGoodsList = setmealGoodsMapper.getBySetmealId(orderDetail.getSetmealId());

                    // 拼接套餐商品信息
                    StringBuilder setmealContent = new StringBuilder();
                    for (SetmealGoods setmealGoods : setmealGoodsList) {
                        setmealContent.append(setmealGoods.getName())
                                .append("*")
                                .append(setmealGoods.getCopies())
                                .append(", ");
                    }
                    if (setmealContent.length() > 0) {
                        setmealContent.delete(setmealContent.length() - 2, setmealContent.length()); // 去掉最后的逗号
                    }

                    // 拼接套餐整体：套餐名(套餐商品...) * 套餐数量
                    String setmealStr = orderDetail.getName() + "(" + setmealContent.toString() + ")"
                            + "*" + orderDetail.getNumber() + ";";
                    orderGoodsList.add(setmealStr);
                } else {
                    // 普通商品
                    String flavorPart = "";
                    if (orderDetail.getGoodsFlavor() != null && !orderDetail.getGoodsFlavor().isEmpty()) {
                        flavorPart = "(" + orderDetail.getGoodsFlavor() + ")";
                    }
                    String goodsStr = orderDetail.getName() + flavorPart + "*" + orderDetail.getNumber() + ";";
                    orderGoodsList.add(goodsStr);
                }
            }
        }

        return String.join("", orderGoodsList);
    }

    @Override
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待付款、待接单、制作中、待派送、派送中的订单数量
        Integer pendingPayment=orderMapper.countStatus(Orders.PENDING_PAYMENT);
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer making = orderMapper.countStatus(Orders.MAKEING);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setPendingPayment(pendingPayment);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setMaking(making);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersConfirmDTO.getId());
        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.MAKEING)
                .build();
        String KEY="reminder"+ordersDB.getNumber();
        if(redisTemplate.opsForValue().get(KEY)!=null){
            redisTemplate.delete(KEY);
        }
        orderMapper.update(orders);
    }

    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == Orders.PAID) {
            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        Orders orders = new Orders();
        if(ordersDB.getPayStatus()==1){
            orders.setPayStatus(2);
        }else{
            orders.setPayStatus(0);
        }
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        //支付状态
        Integer payStatus = ordersDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        if(payStatus==1){
            orders.setPayStatus(2);
        }else{
            orders.setPayStatus(0);
        }
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    public void delivery(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为9
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为派送中
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4||9
        if (ordersDB == null || !(ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS) ||
                ordersDB.getStatus().equals(Orders.MAKEING))){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态
        if(ordersDB.getStatus()==9 &&ordersDB.getDeliveryMethod()==1){
            orders.setStatus(Orders.CONFIRMED);// 设置为待派送
        }else{
            orders.setStatus(Orders.COMPLETED);// 已完成
        }
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 用户催单
     */
    @Override
    public void reminder(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        String KEY="reminder"+ordersDB.getNumber();
        if(redisTemplate.opsForValue().get(KEY)!=null){
            throw new OrderBusinessException(MessageConstant.ORDER_REPEAT_REMIND);
        }
        redisTemplate.opsForValue().set(KEY,id,5, TimeUnit.MINUTES);

        Map map = new HashMap();
        map.put("type", 2); // 1表示来单提醒 2表示客户催单
        map.put("orderId", id);
        map.put("content", "订单号: " + ordersDB.getNumber());

        // 通过websocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
