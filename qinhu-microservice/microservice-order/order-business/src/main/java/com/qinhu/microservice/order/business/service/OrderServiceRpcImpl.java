package com.qinhu.microservice.order.business.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.qinhu.common.core.exception.BusinessExceptionEnum;
import com.qinhu.common.core.model.BaseResponse;
import com.qinhu.common.core.model.WrapperQuery;
import com.qinhu.microservice.good.api.model.IGoodServiceRpc;
import com.qinhu.microservice.order.api.event.OrderDomainEvent;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ChangeOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import com.qinhu.microservice.order.api.service.IOrderServiceRpc;
import com.qinhu.microservice.order.business.WrapperQueryJpaResolver;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.domain.eventpublisher.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSaga;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSagaState;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.micrometer.core.annotation.Timed;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: 订单PRC实现
 * @author: qh
 * @create: 2020-07-05 15:05
 **/
@DubboService(interfaceName = "IOrderServiceRpc", version = "0.0.1", document = "订单服务")
public class OrderServiceRpcImpl implements IOrderServiceRpc {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDomainEventPublisher domainEventPublisher;
    /**
     * Saga构建工厂
     */
    @Autowired
    SagaInstanceFactory sagaInstanceFactory;
    @Autowired
    ConfirmOrderSaga confirmOrderSaga;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Timed(value = "IOrderServiceRpc.createOrder", percentiles = {0.5, 0.7, 0.9},
            description = "监控当前方法所有请求中50%、70%、90%的执行时间(50%->4个请求中的2个)")
    public BaseResponse<OrderVo> createOrder(final CreateOrderQuery createOrderQuery) {

        //领域实体入库
        final ResultWithDomainEvents<Order, OrderDomainEvent> resultWithDomainEvents = Order
                .createOrder(createOrderQuery);
        final Order order = resultWithDomainEvents.result;
        orderRepository.save(order);

        //发布事件到DB      //TODO 接收者为商品服务  预减库存
        domainEventPublisher.publish(order, resultWithDomainEvents.events);

        return BaseResponse.okData(order.toOrderVo());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<OrderVo> editOrder(ChangeOrderQuery orderQuery) {
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(orderQuery.getOrderNo());
        //现在判断订单存在
        Optional<Order> optional = orderRepository.findOne((Specification<Order>) (root, query, criteriaBuilder) -> {
            Path<String> order_no = root.get("order_no");
            Predicate predicate = criteriaBuilder.equal(order_no, orderQuery.getOrderNo());
            return predicate;
        });
        if (!optional.isPresent()) {
            return BaseResponse.errorMsg("订单不存在！");
        }
        final Order originalOrder = optional.get();
        ResultWithDomainEvents<Order, OrderDomainEvent> orderChangeEvent = Order.changeOrder(originalOrder, orderQuery);
        Order updateOrder = orderChangeEvent.result;
        updateOrder.setId(originalOrder.getId());
        orderRepository.save(updateOrder);
        //todo 订单完成推送kafka ===> 有个消费记录
        return BaseResponse.okData(updateOrder.toOrderVo());
    }


    @Override
    public BaseResponse<Long> countOrdersByDate(Long start, Long end, OrderStatus status, String sellerId) {

        BusinessExceptionEnum.BAD_TIME.assertTimeVail(start, end);
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(sellerId);
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotNull(status);

        long count = orderRepository.count((root, query, criteriaBuilder) -> {
            //时间段
            Path<Long> create_time = root.get("create_time");
            Predicate createTime = criteriaBuilder.between(create_time, start, end);
            //订单状态
            Path<String> order_status = root.get("order_status");
            Predicate orderStatus = criteriaBuilder.equal(order_status, status.getName());
            //店铺id为
            Path<String> seller_id = root.get("goods");
            String dbSellerId = "ownerId:" + sellerId;
            Predicate sellerId1 = criteriaBuilder.like(seller_id, dbSellerId);

            Predicate and = criteriaBuilder.and(createTime, orderStatus, sellerId1);
            return and;
        });
        return BaseResponse.okData(count);
    }

    @Override
    public BaseResponse<List<OrderVo>> getListcomplex(WrapperQuery wrapperQuery) {

        Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
        List<Order> all = orderRepository.findAll(new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery), sort);
        List<OrderVo> rts = new ArrayList<>(all.size());
        for (Order order : all) {
            OrderVo orderVo = order.toOrderVo();
            rts.add(orderVo);
        }
        return BaseResponse.okData(rts);
    }

//    @Override
//    public OrderResponse getOrdersByUserId(String userId, OrderStatus status) {
//
//        QueryWrapper qw = Wrappers.<Orders>query();
//
//        if (StrUtil.isEmpty(userId)) {
//            return OrderResponse.error("userId必传");
//        }
//        qw.eq("user_id", userId);
//        qw.orderByDesc("create_time");
//        if (!OrderStatus.ALL.name().equals(status.name())) {
//            qw.eq("order_status", status.name());
//        }
//        try {
//
//            List<Orders> list = this.list(qw);
//            return OrderResponse.ok(list);
//        } catch (Exception ex) {
//            return OrderResponse.error("查询失败");
//        }
//    }
//
//    @Override
//    public OrderResponse delOrders(String orderNo, Map<String, Object> condition) {
//
//        QueryWrapper uw = new QueryWrapper();
//
//        if (StrUtil.isEmpty(orderNo)) {
//            throw new ParameterException("订单号不能为空!");
//        }
//        uw.eq("order_no", orderNo);
//        if (condition != null && condition.size() != 0) {
//            for (String key : condition.keySet()) {
//                uw.eq(key, condition.get(key));
//            }
//        }
//        try {
//            this.remove(uw);
//            return OrderResponse.ok("删除成功");
//        } catch (Exception ex) {
//            return OrderResponse.error("删除失败");
//        }
//    }
//
//    @Override
//    public OrderResponse cancelOrder(String orderNo) {
//
//        if (StrUtil.isEmpty(orderNo)) {
//            throw new ParameterException("订单号不能为空!");
//        }
//
//        UpdateWrapper uw = new UpdateWrapper();
//
//        uw.eq("order_no", orderNo);
//
//        uw.set("order_status", OrderStatus.CANCELLED.name());
//        try {
//            this.update(uw);
//            return OrderResponse.ok("取消订单成功");
//        } catch (Exception ex) {
//            return OrderResponse.error("取消订单失败");
//        }
//    }
//
//    @Override
//    public OrderResponse getOneOrder(Map<String, Object> condition) {
//        QueryWrapper uw = new QueryWrapper();
//
//        if (condition != null && condition.size() != 0) {
//            for (String key : condition.keySet()) {
//                uw.eq(key, condition.get(key));
//            }
//        }
//
//        try {
//            Orders orders = this.getOne(uw);
//            return OrderResponse.ok("获取订单成功", orders);
//        } catch (Exception ex) {
//            return OrderResponse.error("获取失败");
//        }
//    }
//
//    @Override
//    public String getPreOrderNo() {
//
//        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
//        String orderNo = snowflake.nextIdStr();
//
//        return orderNo;
//    }
//
//
//    @Override
//    public long getCount(QueryWrapper ew) {
//
//        return super.count(ew);
//    }
//
//    @Override
//    public OrderResponse page(PageWrapperQuery pageWrapperQuery) {
//
//        IPage ipage = new Page(pageWrapperQuery.getPageQuery().getPageNo(), pageWrapperQuery.getPageQuery().getPageSize());
//        QueryWrapper queryWrapper = PageWrapperQueryUtil.getQueryWrapper(pageWrapperQuery);
//        queryWrapper.orderByDesc("create_time");
//        try {
//
//            Page<Orders> page = (Page<Orders>) this.page(ipage, queryWrapper);
//            OrderResponse orderResponse = OrderResponse.ok("分页查询成功");
//            orderResponse.setPage(page);
//            return orderResponse;
//        } catch (Exception ex) {
//            OrderResponse orderResponse = OrderResponse.error("分页查询失败");
//            return orderResponse;
//        }
//
//    }
//
//    @Override
//    public boolean updateBatch(List<OrderDetailQuery> ordersCancels) {
//
//        if (ordersCancels == null || ordersCancels.size() == 0) {
//            return true;
//        }
//        List<Orders> ordersUpdate = new ArrayList<>(ordersCancels.size());
//        for (OrderDetailQuery orderDetailQuery : ordersCancels) {
//            ordersUpdate.add(OrderDetailQuery2Orders(orderDetailQuery));
//        }
//
//        return this.updateBatchById(ordersUpdate);
//    }
//
//    @Override
//    public BaseResponse statisticsTop(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end) {
//
//        //金额统计
//        BigDecimal tradeMoney = iOrderStatisticsStrategy.statisticsTradeMoney(shopName, start, end);
//        BigDecimal refundMoney = iOrderStatisticsStrategy.statisticsRefundMoney(shopName, start, end);
//        BigDecimal tradeSubRefundMoney = tradeMoney.subtract(refundMoney);
//
//        //笔数统计
//        Integer tradeCount = iOrderStatisticsStrategy.statisticsTradeCount(shopName, start, end);
//        Integer refundCount = iOrderStatisticsStrategy.statisticsRefundCount(shopName, start, end);
//        Map<String, Object> rts = new HashMap<>();
//        rts.put("交易金额", tradeMoney.doubleValue());
//        rts.put("交易笔数", tradeCount.intValue());
//        rts.put("退款金额", refundMoney.doubleValue());
//        rts.put("退款笔数", refundCount.intValue());
//        rts.put("收入金额", tradeSubRefundMoney.doubleValue());
//        BaseResponse baseResponse = BaseResponse.ok(rts);
//        baseResponse.setClassName(Orders.class.getName());
//        return baseResponse;
//    }
//
//    @Override
//    public BaseResponse statisticsByChart(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end) {
//
//        //交易折线图数据
//        Map<String, BigDecimal> tradeMoneyChartData = iOrderStatisticsStrategy.statisticsTradeMoneyChart(shopName, start, end);
//        Map<String, Integer> tradeCountChartData = iOrderStatisticsStrategy.statisticsTradeCountChart(shopName, start, end);
//
//        //退款折线图数据
//        Map<String, BigDecimal> refundMoneyChartData = iOrderStatisticsStrategy.statisticsRefundMoneyChart(shopName, start, end);
//        Map<String, Integer> refundCountChartData = iOrderStatisticsStrategy.statisticsRefundCountChart(shopName, start, end);
//        Map<String, Map> rts = new HashMap<>();
//
//        rts.put("交易金额", tradeMoneyChartData);
//        rts.put("交易笔数", tradeCountChartData);
//
//        rts.put("退款金额", refundMoneyChartData);
//        rts.put("退款笔数", refundCountChartData);
//        BaseResponse baseResponse = BaseResponse.ok(rts);
//        baseResponse.setClassName(Orders.class.getName());
//        return baseResponse;
//
//    }
//
//    @Override
//    public BaseResponse statisticsCountPayMethod(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end) {
//
//        Map<String, Integer> countByPayMenthod = iOrderStatisticsStrategy.statisticsCountByPayMenthod(shopName, start, end);
//        BaseResponse baseResponse = BaseResponse.ok(countByPayMenthod);
//        baseResponse.setClassName(Orders.class.getName());
//        return baseResponse;
//    }
//
//    @Override
//    public BaseResponse statisticsMoneyPayMethod(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end) {
//
//        Map<String, BigDecimal> moneyByPayMenthod = iOrderStatisticsStrategy.statisticsMoneyByPayMenthod(shopName, start, end);
//        Map<String, Double> moneyByPayMenthodDo = new HashMap<>(moneyByPayMenthod.size());
//        for (String key : moneyByPayMenthod.keySet()
//        ) {
//            if (StrUtil.isEmpty(key)) {
//                continue;
//            }
//            moneyByPayMenthodDo.put(key, moneyByPayMenthod.get(key).doubleValue());
//        }
//        BaseResponse baseResponse = BaseResponse.ok(moneyByPayMenthodDo);
//        baseResponse.setClassName(Orders.class.getName());
//        return baseResponse;
//    }
//
//    @Override
//    public BaseResponse statisticsList(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end, int payment) {
//        List<Orders> list = iOrderStatisticsStrategy.statisticsList(shopName, start, end, payment);
//        BaseResponse baseResponse = BaseResponse.ok(new Orders());
//        baseResponse.setList(list);
//        return baseResponse;
//    }
//
//
//    @Override
//    public BaseResponse statisticsAggregate(PageWrapperQuery pageWrapperQuery) {
//
//        QueryWrapper qw = PageWrapperQueryUtil.getQueryWrapper(pageWrapperQuery);
//        qw.eq("pay_status", "PAY_YES");
//        qw.eq("paas", "cashier");
//        List<Orders> list = this.list(qw);
//
//        //返回结果
//        HashMap<String, BigDecimal> rts = MapUtil.newHashMap(2);
//        rts.put("Aggregate", new BigDecimal(0));
//        rts.put("Money", new BigDecimal(0));
//        rts.put("Discounts", new BigDecimal(0));
//
//        if (list.size() == 0) {
//            return BaseResponse.ok(rts);
//        }
//
//        //统计聚合支付的金额
//        List<Orders> aggregateList = list.stream()
//                .filter(arg -> !"现金支付".equals(arg.getPaymentMethodName()))
//                .collect(Collectors.toList());
//        for (Orders order : aggregateList) {
//            BigDecimal amount = order.getPayPrice();
//            BigDecimal aggregateTotal = rts.get("Aggregate");
//            rts.put("Aggregate", aggregateTotal.add(amount));
//        }
//        //统计现金支付的金额
//        List<Orders> moneyList = list.stream()
//                .filter(arg -> "现金支付".equals(arg.getPaymentMethodName()))
//                .collect(Collectors.toList());
//        for (Orders order : moneyList) {
//            BigDecimal amount = order.getPayPrice();
//            BigDecimal aggregateTotal = rts.get("Money");
//            rts.put("Money", aggregateTotal.add(amount));
//        }
//        //统计优惠金额
//        for (Orders order : list) {
//            BigDecimal discountAmount = order.getDiscount();
//            BigDecimal discountTotal = rts.get("Discounts");
//            rts.put("Discounts", discountTotal.add(discountAmount));
//        }
//
//        return BaseResponse.ok(rts);
//    }
//
//    @Override
//    public BaseResponse<List<StatisticsGoods>> statisticsGoodsMarketMoneyGroupSellerGood(PageWrapperQuery pageWrapperQuery) {
//
//        //获取当天所有订单
//        QueryWrapper qw = PageWrapperQueryUtil.getQueryWrapper(pageWrapperQuery);
//        qw.eq("paas", "cashier");
//        qw.eq("pay_status", "PAY_YES");
//        List<Orders> list = this.list(qw);
//
//        //统计商品集合
//        List<StatisticsGoods> rts = new ArrayList<>();
//        for (Orders order : list) {
//
//            String goodsStr = order.getGoods();
//            List<StatisticsGoods> statisticsGoods = JSON.parseArray(goodsStr, StatisticsGoods.class);
//            for (StatisticsGoods good : statisticsGoods) {
//                good.setSellerId(order.getSellerId());
//            }
//            rts.addAll(statisticsGoods);
//        }
//
//        return BaseResponse.ok(rts);
//    }
//
//    /**
//     * query转Orders实体
//     *
//     * @param ordersCancels
//     * @return
//     */
//    private Orders OrderDetailQuery2Orders(OrderDetailQuery ordersCancels) {
//
//        Orders orders = Orders.builder()
//                .id(ordersCancels.getId())
//                .orderStatus(ordersCancels.getStatus())
//                .paas(ordersCancels.getPaas())
//                .payStatus(ordersCancels.getPayStatus())
//                .remark(ordersCancels.getRemark())
//                .build();
//
//        //值类型 判断是否为0,选择更新
//        if (ordersCancels.getTotalPrice() != 0) {
//            orders.setTotalPrice(new BigDecimal(ordersCancels.getTotalPrice()));
//        }
//        if (ordersCancels.getPayPrice() != 0) {
//            orders.setPayPrice(new BigDecimal(ordersCancels.getPayPrice()));
//        }
//        return orders;
//    }
//
//    @Override
//    public OrderResponse payOrder(String orderNo) {
//        OrderEventResult.builder()
//                .build();
//        return null;
//    }
//
//
//    /**
//     * 判断订单参数有效否
//     *
//     * @param orderQuery
//     * @return
//     */
//    private boolean checkOrder(OrderDetailQuery orderQuery) {
//
//        return orderQuery.getGoods().size() != 0
//                && orderQuery.getSellerId() != 0
//                && !(orderQuery.getTotalPrice() < 0);
//    }
//
//    /**
//     * 获取map的valus
//     *
//     * @param map 目标map
//     * @return list
//     */
//    private <T> List<T> getMapValues(Map<String, T> map) {
//
//        List list = CollectionUtil.newArrayList();
//        for (String key : map.keySet()) {
//            list.add(map.get(key));
//        }
//
//        return list;
//    }
//
//    /**
//     * 订单完成时 发布事件
//     */
//    private void orderCompleteEvent(Orders orders) {
//        if (orders == null) {
//            throw new RuntimeException();
//        }
//        String msg = JSON.toJSONString(orders);
//        producer.send("create_xpense", msg);
//    }


//=========================================================================================================


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo confirm(final ChangeOrderQuery changeOrderQuery) {

        Optional<Order> optional = orderRepository.findOne((Specification<Order>) (root, query, criteriaBuilder) -> {
            root.get("order_no");
            return criteriaBuilder.equal(root, changeOrderQuery.getOrderNo());
        });

        if (optional.isPresent()) {
            final Order order = optional.get();
            ResultWithDomainEvents<Order, OrderDomainEvent> confirmOrder = order.confirmOrder();
            Order updateOrder = confirmOrder.result;
            orderRepository.save(updateOrder);
            //商户发货事件通知
            domainEventPublisher.publish(order, confirmOrder.events);

            /*
             * Saga的调用
             *   1.构建ConfirmOrder的Saga状态机
             *   2.使用sagaInstanceFactory构建SagaInstance
             *   3.框架自动将SagaInstance托管给SagaManager去调用
             * */
            BigDecimal totalPrice = order.getTotalPrice();
            ConfirmOrderSagaState confirmOrderSagaState = new ConfirmOrderSagaState("000001", totalPrice, new ArrayList<>());

            sagaInstanceFactory.create(confirmOrderSaga, confirmOrderSagaState);

            return order.toOrderVo();
        } else {
            BusinessExceptionEnum.BUSINESS_ERROR.assertNotNull(null);
            return null;
        }
    }

}
