package com.qinhu.microservice.order.business.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 策略   每日统计
 * @author: qh
 * @create: 2020-01-02 15:44
 **/
@SuppressWarnings("all")
public class OrderStatisticsStrategyDay extends OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {

    /**
     * 每小时换算ms
     */
    private static final long LONGHOUR = 60 * 60 * 1000;

    public OrderStatisticsStrategyDay(OrderRepository orderRepository) {
        super(orderRepository);
    }


    @Override
    public Map<String, BigDecimal> statisticsMoneyByPayMenthod(String shopName, long start, long end) {

        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsMoneyByPayMenthod(shopName, start, end);
    }

    @Override
    public Map<String, Integer> statisticsCountByPayMenthod(String shopName, long start, long end) {

        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsCountByPayMenthod(shopName, start, end);
    }

    @Override
    public BigDecimal statisticsTradeMoney(String shopName, long start, long end) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsMoney(shopName, start, end, PayYESCondition);
    }

    @Override
    public Integer statisticsTradeCount(String shopName, long start, long end) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsCount(shopName, start, end, PayYESCondition);
    }

    @Override
    public BigDecimal statisticsRefundMoney(String shopName, long start, long end) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsMoney(shopName, start, end, OrderStatusCondition);
    }

    @Override
    public Integer statisticsRefundCount(String shopName, long start, long end) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        return super.statisticsCount(shopName, start, end, OrderStatusCondition);
    }

    @Override
    public Map<String, BigDecimal> statisticsTradeMoneyChart(String shopName, long start, long end) {

        return statisticsMoneyChart(shopName, start, end, super.PayYESCondition);
    }

    @Override
    public Map<String, Integer> statisticsTradeCountChart(String shopName, long start, long end) {
        return statisticsCountChart(shopName, start, end, super.PayYESCondition);
    }

    @Override
    public Map<String, Integer> statisticsRefundCountChart(String shopName, long start, long end) {
        return statisticsCountChart(shopName, start, end, super.OrderStatusCondition);
    }

    @Override
    public Map<String, BigDecimal> statisticsRefundMoneyChart(String shopName, long start, long end) {

        return statisticsMoneyChart(shopName, start, end, super.OrderStatusCondition);
    }

    @Override
    public List<Order> statisticsList(String shopName, long start, long end, int payment) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        Map<String, String> condition = new HashMap<>(2);
        BeanUtil.copyProperties(super.PayYESCondition, condition);
        String paymentName = super.getPaymentName(payment);
        condition.put("payment_method_name", paymentName);
        return getListOnce(shopName, start, end, condition);
    }

    /**
     * 统计每小时的金额走向
     */
    private Map<String, BigDecimal> statisticsMoneyChart(String shopName, long start, long end, Map<String, String> codition) {

        start = getDefaultStart(start);
        end = getDefaultEnd(end);

        List<Order> list = getListOnce(shopName, start, end, codition);
        //当天开始时间
        long dayStart = DateUtil.beginOfDay(DateUtil.date(start)).getTime();
        Map<String, BigDecimal> map = super.initMoneyMap(24);
        //如果查询的条件是订单状态  过滤非订单状态
        if (codition.containsKey(OrdersDBColumn.ORDER_STATUS)) {
            list = list.stream()
                    .filter(arg ->
                            codition.get(OrdersDBColumn.ORDER_STATUS).equals(arg.getOrderStatus())
                    ).collect(Collectors.toList());
        }
        //更具每小时处理数据
        list.stream().forEach(arg -> {
            int hour = (int) ((Math.ceil(arg.getUpdateTime().getTime() - dayStart) / LONGHOUR)) + 1;
            map.put(hour + "", map.get(hour + "").add(arg.getPayPrice()));
        });
        return sortByKey(map);
    }

    /**
     * 统计每小时的笔数走向
     */
    private Map<String, Integer> statisticsCountChart(String shopName, long start, long end, Map codition) {

        start = getDefaultStart(start);
        end = getDefaultEnd(end);

        List<Order> list = getListOnce(shopName, start, end, codition);
        //当天开始时间
        long dayStart = DateUtil.beginOfDay(DateUtil.date(start)).getTime();
        Map<String, Integer> map = super.initCountMap(24);
        //如果查询的条件是订单状态  过滤非订单状态
        if (codition.containsKey(OrdersDBColumn.ORDER_STATUS)) {
            list = list.stream()
                    .filter(arg ->
                            codition.get(OrdersDBColumn.ORDER_STATUS).equals(arg.getOrderStatus())
                    ).collect(Collectors.toList());
        }
        //更具每小时处理数据
        list.stream().forEach(arg -> {
            int hour = (int) ((Math.ceil(arg.getUpdateTime().getTime() - dayStart) / LONGHOUR)) + 1;
            map.put(hour + "", map.get(hour + "") + 1);
        });
        return sortByKey(map);
    }


    /**
     * 获取默认开始时间,默认为今天0:0:0 MS
     *
     * @param start 开始时间
     * @return
     */
    private static long getDefaultStart(long start) {
        //如果没传时间进来   默认开始时间是今天开始时间
        if (start == 0) {
            start = System.currentTimeMillis();
            start = DateUtil.beginOfDay(DateUtil.date(start)).getTime();
        }

        return start;
    }

    /**
     * 获取默认结束时间,默认为当前时间  MS
     *
     * @param end 结束时时间
     * @return
     */
    private static long getDefaultEnd(long end) {
        //如果没传时间进来   默认开始时间是今天开始时间
        if (end == 0) {
            end = System.currentTimeMillis();
        }

        return end;
    }
}
