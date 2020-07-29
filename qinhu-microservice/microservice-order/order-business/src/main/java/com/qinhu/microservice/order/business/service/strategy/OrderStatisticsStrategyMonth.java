package com.qinhu.microservice.order.business.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.qinhu.microservice.order.api.model.PaymentName;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 策略  每月统计
 * @author: qh
 * @create: 2020-01-02 17:57
 **/
@SuppressWarnings("all")
public class OrderStatisticsStrategyMonth extends OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {

    /**
     * 每天时长转MS
     */
    private static final long LONGDAY = 24 * 60 * 60 * 1000;

    public OrderStatisticsStrategyMonth(OrderRepository orderRepository) {
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
     * 统计每天的金额走向
     *
     * @return
     */
    private Map<String, BigDecimal> statisticsMoneyChart(String shopName, long start, long end, Map codition) {

        //获取默认时间
        start = getDefaultStart(start);
        end = getDefaultEnd(end);

        List<Order> list = getListOnce(shopName, start, end, codition);
        //当月开始时间
        long monthStart = DateUtil.beginOfMonth(DateUtil.date(start)).getTime();
        Map<String, BigDecimal> map = super.initMoneyMap(31);
        list.stream().forEach(arg -> {
            int day = (int) (Math.ceil(arg.getUpdateTime().getTime() - monthStart) / LONGDAY) + 1;
            map.put(day + "", map.get(day + "").add(arg.getPayPrice()));
        });

        return sortByKey(map);
    }


    /**
     * 统计每天的笔数走向
     *
     * @return
     */
    private Map<String, Integer> statisticsCountChart(String shopName, long start, long end, Map codition) {
        //获取默认时间
        start = getDefaultStart(start);
        end = getDefaultEnd(end);

        List<Order> list = getListOnce(shopName, start, end, codition);
        //当月开始时间
        long monthStart = DateUtil.beginOfMonth(DateUtil.date(start)).getTime();
        Map<String, Integer> map = super.initCountMap(31);
        list.stream().forEach(arg -> {
            int day = (int) Math.ceil((arg.getUpdateTime().getTime() - monthStart) / LONGDAY) + 1;
            map.put(day + "", map.get(day + "") + 1);
        });

        return sortByKey(map);
    }


    /**
     * 获取默认开始时间,默认为这个月 xx月01号0:0:0 MS
     *
     * @param start 开始时间
     * @return
     */
    private static long getDefaultStart(long start) {
        //如果没传时间进来   默认开始时间是今天开始时间
        if (start == 0) {
            start = System.currentTimeMillis();
            start = DateUtil.beginOfMonth(DateUtil.date(start)).getTime();
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
