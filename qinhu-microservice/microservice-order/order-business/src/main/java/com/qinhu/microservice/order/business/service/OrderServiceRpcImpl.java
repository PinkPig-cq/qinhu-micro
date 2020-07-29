package com.qinhu.microservice.order.business.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.qinhu.common.core.exception.BusinessExceptionEnum;
import com.qinhu.common.core.model.BaseResponse;
import com.qinhu.common.core.model.WrapperQuery;
import com.qinhu.microservice.order.api.event.OrderDomainEvent;
import com.qinhu.microservice.order.api.model.CommodityVoOrderCopy;
import com.qinhu.microservice.order.api.model.FrontType;
import com.qinhu.microservice.order.api.model.OrderPayStatus;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ChangeOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import com.qinhu.microservice.order.api.service.IOrderServiceRpc;
import com.qinhu.microservice.order.business.PageUtil;
import com.qinhu.microservice.order.business.WrapperQueryJpaResolver;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.domain.eventpublisher.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSaga;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSagaState;
import com.qinhu.microservice.order.business.service.strategy.IOrderStatisticsStrategy;
import com.qinhu.microservice.order.business.service.strategy.OrderStatisticsStrategyUtil;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.micrometer.core.annotation.Timed;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
@Service
@DubboService(interfaceName = "IOrderServiceRpc", version = "0.0.1", document = "订单服务")
public class OrderServiceRpcImpl implements IOrderServiceRpc {

    final
    OrderRepository orderRepository;
    final
    OrderDomainEventPublisher domainEventPublisher;
    /**
     * Saga构建工厂
     */
    final
    SagaInstanceFactory sagaInstanceFactory;
    final
    ConfirmOrderSaga confirmOrderSaga;

    final Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

    public OrderServiceRpcImpl(OrderRepository orderRepository, OrderDomainEventPublisher domainEventPublisher, SagaInstanceFactory sagaInstanceFactory, ConfirmOrderSaga confirmOrderSaga) {
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.sagaInstanceFactory = sagaInstanceFactory;
        this.confirmOrderSaga = confirmOrderSaga;
    }


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
    public BaseResponse<OrderVo> editOrder(final ChangeOrderQuery orderQuery) {
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(orderQuery.getOrderNo());
        //现在判断订单存在
        Optional<Order> optional = orderRepository.findOne((Specification<Order>) (root, query, criteriaBuilder) -> {
            Path<String> orderNo = root.get("orderNo");
            return criteriaBuilder.equal(orderNo, orderQuery.getOrderNo());
        });
        if (!optional.isPresent()) {
            return BaseResponse.errorMsg("订单不存在！");
        }
        final Order originalOrder = optional.get();
        final ResultWithDomainEvents<Order, OrderDomainEvent> orderChangeEvent = Order.changeOrder(originalOrder, orderQuery);
        Order updateOrder = orderChangeEvent.result;
        updateOrder.setId(originalOrder.getId());
        orderRepository.save(updateOrder);
        //todo 订单完成推送kafka ===> 有个消费记录
        return BaseResponse.okData(updateOrder.toOrderVo());
    }


    @Override
    public BaseResponse<Long> countOrdersByDate(final Date start, final Date end,
                                                final OrderStatus status, final String sellerId) {

        BusinessExceptionEnum.BAD_TIME.assertTimeVail(start, end);
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(sellerId);
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotNull(status);

        long count = orderRepository.count((root, query, criteriaBuilder) -> {
            //时间段
            Path<Date> createTimePath = root.get("createTime");
            Predicate createTime = criteriaBuilder.between(createTimePath, start, end);
            //订单状态
            Path<String> orderStatusPath = root.get("orderStatus");
            Predicate orderStatus = criteriaBuilder.equal(orderStatusPath, status);
            //店铺id为
            Path<String> sellerIdPath = root.get("goods");
            String dbSellerId = "%\"ownerId\":" + sellerId + "%";
            Predicate sellerIdPred = criteriaBuilder.like(sellerIdPath, dbSellerId);

            return criteriaBuilder.and(createTime, orderStatus, sellerIdPred);
        });
        return BaseResponse.okData(count);
    }

    @Override
    public BaseResponse<List<OrderVo>> getListComplex(final WrapperQuery wrapperQuery) {
        List<Order> all;

        if (wrapperQuery == null) {
            all = orderRepository.findAll();
        } else {
            all = orderRepository.findAll(new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery), sort);
        }

        List<OrderVo> rts = new ArrayList<>(all.size());
        for (Order order : all) {
            OrderVo orderVo = order.toOrderVo();
            rts.add(orderVo);
        }
        return BaseResponse.okData(rts);
    }


    @Override
    public BaseResponse<org.apache.dubbo.common.utils.Page<OrderVo>> page(final WrapperQuery wrapperQuery) {

        Specification<Order> spasCredential = new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery);
        final Pageable pageable = PageRequest.of(
                wrapperQuery.getPageQuery().getPageNo(),
                wrapperQuery.getPageQuery().getPageSize(), sort);
        final Page<Order> orderPage = orderRepository.findAll(spasCredential, pageable);
        List<OrderVo> list = new ArrayList<>(orderPage.getContent().size());
        for (Order order : orderPage.getContent()) {
            OrderVo orderVo = order.toOrderVo();
            list.add(orderVo);
        }
        final Page<OrderVo> page = new PageImpl<>(list, pageable, orderPage.getTotalElements());
        org.apache.dubbo.common.utils.Page<OrderVo> rts = PageUtil.jpaPage2DubboPage(page);
        return BaseResponse.okData(rts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<ChangeOrderQuery> queryList) {

        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertCollectionNotILLEGAL(queryList);
        for (ChangeOrderQuery changeOrderQuery : queryList) {
            BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotNull(changeOrderQuery.getId());
        }
        List<Order> orderUpdate = new ArrayList<>(queryList.size());
        for (ChangeOrderQuery changeOrderQuery : queryList) {
            orderUpdate.add(Order.changeOrder(null, changeOrderQuery).result);
        }

        return orderRepository.saveAll(orderUpdate).size() != 0;
    }

    @Override
    public BaseResponse<Map<String, Double>> topStatistics(final Integer orderStatisticsStrategy,
                                                           final String shopName, final Date start,
                                                           final Date end) {

        IOrderStatisticsStrategy iOrderStatisticsStrategy = OrderStatisticsStrategyUtil.getStrategy(orderStatisticsStrategy, this.orderRepository);
        //金额统计
        BigDecimal tradeMoney = iOrderStatisticsStrategy.statisticsTradeMoney(shopName, start.getTime(), end.getTime());
        BigDecimal refundMoney = iOrderStatisticsStrategy.statisticsRefundMoney(shopName, start.getTime(), end.getTime());
        BigDecimal tradeSubRefundMoney = tradeMoney.subtract(refundMoney);
        //笔数统计
        Integer tradeCount = iOrderStatisticsStrategy.statisticsTradeCount(shopName, start.getTime(), end.getTime());
        Integer refundCount = iOrderStatisticsStrategy.statisticsRefundCount(shopName, start.getTime(), end.getTime());
        Map<String, Double> rts = new HashMap<>(5);
        rts.put("交易金额", tradeMoney.doubleValue());
        rts.put("交易笔数", tradeCount.doubleValue());
        rts.put("退款金额", refundMoney.doubleValue());
        rts.put("退款笔数", refundCount.doubleValue());
        rts.put("收入金额", tradeSubRefundMoney.doubleValue());
        return BaseResponse.okData(rts);
    }

    @Override
    public BaseResponse<Map<String, Map>> tradeStatistics(final Integer orderStatisticsStrategy,
                                                          final String shopName, final Date start,
                                                          final Date end) {

        IOrderStatisticsStrategy iOrderStatisticsStrategy = OrderStatisticsStrategyUtil.getStrategy(orderStatisticsStrategy, this.orderRepository);

        //交易折线图数据
        Map<String, BigDecimal> tradeMoneyChartData = iOrderStatisticsStrategy.statisticsTradeMoneyChart(shopName, start.getTime(), end.getTime());
        Map<String, Integer> tradeCountChartData = iOrderStatisticsStrategy.statisticsTradeCountChart(shopName, start.getTime(), end.getTime());

        //退款折线图数据
        Map<String, BigDecimal> refundMoneyChartData = iOrderStatisticsStrategy.statisticsRefundMoneyChart(shopName, start.getTime(), end.getTime());
        Map<String, Integer> refundCountChartData = iOrderStatisticsStrategy.statisticsRefundCountChart(shopName, start.getTime(), end.getTime());
        Map<String, Map> rts = new HashMap<>(4);

        rts.put("交易金额", tradeMoneyChartData);
        rts.put("交易笔数", tradeCountChartData);

        rts.put("退款金额", refundMoneyChartData);
        rts.put("退款笔数", refundCountChartData);
        return BaseResponse.okData(rts);
    }

    @Override
    public BaseResponse<Map<String, Integer>> countByPayMethodStatistics(final Integer orderStatisticsStrategy,
                                                                         final String shopName, final Date start,
                                                                         final Date end) {
        IOrderStatisticsStrategy iOrderStatisticsStrategy = OrderStatisticsStrategyUtil.getStrategy(orderStatisticsStrategy, this.orderRepository);
        Map<String, Integer> countByPayMenthod = iOrderStatisticsStrategy.statisticsCountByPayMenthod(shopName, start.getTime(), end.getTime());
        return BaseResponse.okData(countByPayMenthod);
    }

    @Override
    public BaseResponse<Map<String, Number>> moneyByPayMethodStatistics(final Integer orderStatisticsStrategy,
                                                                        final String shopName, final Date start,
                                                                        final Date end) {
        IOrderStatisticsStrategy iOrderStatisticsStrategy = OrderStatisticsStrategyUtil.getStrategy(orderStatisticsStrategy, this.orderRepository);

        Map<String, BigDecimal> moneyByPayMenthod = iOrderStatisticsStrategy.statisticsMoneyByPayMenthod(shopName, start.getTime(), end.getTime());
        Map<String, Number> moneyByPayMenthodDo = new HashMap<>(moneyByPayMenthod.size());
        for (String key : moneyByPayMenthod.keySet()
        ) {
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            moneyByPayMenthodDo.put(key, moneyByPayMenthod.get(key).doubleValue());
        }
        return BaseResponse.okData(moneyByPayMenthodDo);
    }


    @Override
    public BaseResponse<Map<String, BigDecimal>> everyDayWorkingCapital(final WrapperQuery wrapperQuery) {

        Specification<Order> queryWrapper = new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery);
        final Specification<Order> otherSpec = (root, query, criteriaBuilder) -> {
            Path<Order> payStatus = root.get("payStatus");
            Predicate pPayStatus = criteriaBuilder.equal(payStatus, "PAY_YES");
            Path<Order> front = root.get("front");
            Predicate pFront = criteriaBuilder.equal(front, FrontType.CASHIER.getName());
            return criteriaBuilder.and(pPayStatus, pFront);
        };
        final Specification<Order> and = queryWrapper.and(otherSpec);
        List<Order> list = orderRepository.findAll(and);
        //返回结果
        HashMap<String, BigDecimal> rts = MapUtil.newHashMap(2);
        rts.put("Aggregate", new BigDecimal(0));
        rts.put("Money", new BigDecimal(0));
        rts.put("Discounts", new BigDecimal(0));
        if (list.size() == 0) {
            return BaseResponse.okData(rts);
        }
        //统计聚合支付的金额
        List<Order> aggregateList = list.stream()
                .filter(arg -> !"现金支付".equals(arg.getPaymentMethodName()))
                .collect(Collectors.toList());
        for (Order order : aggregateList) {
            BigDecimal amount = order.getPayPrice();
            BigDecimal aggregateTotal = rts.get("AggregatePay");
            rts.put("AggregatePay", aggregateTotal.add(amount));
        }
        //统计现金支付的金额
        List<Order> moneyList = list.stream()
                .filter(arg -> "现金支付".equals(arg.getPaymentMethodName()))
                .collect(Collectors.toList());
        for (Order order : moneyList) {
            BigDecimal amount = order.getPayPrice();
            BigDecimal aggregateTotal = rts.get("MoneyPay");
            rts.put("MoneyPay", aggregateTotal.add(amount));
        }
        //统计优惠金额
        for (Order order : list) {
            BigDecimal discountAmount = order.getDiscount();
            BigDecimal discountTotal = rts.get("Discounts");
            rts.put("Discounts", discountTotal.add(discountAmount));
        }
        return BaseResponse.okData(rts);
    }

    @Override
    public BaseResponse<List<CommodityVoOrderCopy>> statisticsGoodsMarketMoneyGroupSellerGood(final WrapperQuery wrapperQuery) {

        Specification<Order> queryWrapper = new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery);
        final Specification<Order> otherSpec = (Specification<Order>) (root, query, criteriaBuilder) -> {
            Path<Object> front = root.get("front");
            Predicate frontEqual = criteriaBuilder.equal(front, FrontType.CASHIER);
            Path<Object> payStatus = root.get("payStatus");
            Predicate payStatusEqual = criteriaBuilder.equal(payStatus, OrderPayStatus.PAY_YES);
            return criteriaBuilder.and(frontEqual, payStatusEqual);
        };
        final Specification<Order> specification = queryWrapper.and(otherSpec);
        List<Order> list = orderRepository.findAll(specification);
        //统计商品集合
        List<CommodityVoOrderCopy> rts = new ArrayList<>();
        for (Order order : list) {
            String goodsStr = order.getGoods();
            List<CommodityVoOrderCopy> statisticsGoods = JSON.parseArray(goodsStr, CommodityVoOrderCopy.class);
            for (CommodityVoOrderCopy good : statisticsGoods) {
                good.setOwner(order.getSellerId());
            }
            rts.addAll(statisticsGoods);
        }
        return BaseResponse.okData(rts);
    }

    @Override
    public long getCount(final WrapperQuery wrapperQuery) {

        final Specification<Order> specification = new WrapperQueryJpaResolver<Order>().getQueryWrapper(wrapperQuery);
        return orderRepository.count(specification);
    }
//=========================================================================================================


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo confirm(final ChangeOrderQuery changeOrderQuery) {

        Optional<Order> optional = orderRepository.findOne((Specification<Order>) (root, query, criteriaBuilder) -> {
            root.get("orderNo");
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

    @Override
    public BaseResponse<OrderVo> oneOrderByNo(String orderNo) {

        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(orderNo);

        Optional<Order> optionalOrder = orderRepository.findOne((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderNo"), orderNo));
        if (optionalOrder.isPresent()) {
            return BaseResponse.okData(optionalOrder.get().toOrderVo());
        }
        return BaseResponse.errorMsg("未找到符合要求的订单!");
    }

}
