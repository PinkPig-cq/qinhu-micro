package com.qinhu.microservice.order.business.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 策略 每季度统计
 * @author: qh
 * @create: 2020-01-02 23:05
 **/

@SuppressWarnings("all")
public class OrderStatisticsStrategyQuarter extends OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {


    public OrderStatisticsStrategyQuarter(OrderRepository orderRepository) {
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
     * 统计每月的金额走向
     *
     * @return
     */
    private Map<String, BigDecimal> statisticsMoneyChart(String shopName, long start, long end, Map codition) {

        start = getDefaultStart(start);
        end = getDefaultEnd(end);

        List<Order> list = getListOnce(shopName, start, end, codition);
        Map<String, BigDecimal> map = initMonthMoneyMap(start, end);
        list.stream().forEach(arg -> {
            addMoney(arg.getUpdateTime().getTime(), map, arg.getPayPrice());
        });

        return map;
    }


    /**
     * 统计每月的笔数走向
     *
     * @return
     */
    private Map<String, Integer> statisticsCountChart(String shopName, long start, long end, Map codition) {
        //如果没传开始时间，默认开始时间为今年1月1日
        if (start == 0) {
            start = DateUtil.beginOfYear(DateUtil.date()).getTime();
        }
        //没有结束时间，默认结束时间为12月31日
        if (end == 0) {
            end = DateUtil.endOfYear(DateUtil.date()).getTime();
        }
        Map<String, Integer> map = initMonthCountMap(start, end);
        List<Order> list = getListOnce(shopName, start, end, codition);
        list.stream().forEach(arg -> {
            addCount(arg.getUpdateTime().getTime(), map);
        });

        return map;
    }

    /**
     * 根据订单时间,判断属于哪个季度,最后更新该季度的交易笔数
     *
     * @param oder 订单时间
     * @param map  季度金额集合
     */
    private void addCount(long oder, Map<String, Integer> map) {

        Date nextDate = DateUtil.endOfQuarter(DateUtil.date(oder));
        map.put(nextDate + "", map.get(nextDate + "") + 1);

    }

    /**
     * 根据订单时间,判断属于哪个季度,最后更新该季度的总金额
     *
     * @param oder   订单时间
     * @param map    季度金额集合
     * @param amount 当前订单金额
     */
    private void addMoney(long oder, Map<String, BigDecimal> map, BigDecimal amount) {

        Date nextDate = DateUtil.endOfQuarter(DateUtil.date(oder));
        map.put(nextDate + "", map.get(nextDate + "").add(amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));

    }

    /**
     * 初始化按季度统计金额的map
     *
     * @param start
     * @param end
     * @return
     */
    private Map<String, BigDecimal> initMonthMoneyMap(long start, long end) {

        //如果没传开始时间，默认开始时间为今年1月1日
        if (start == 0) {
            start = DateUtil.beginOfYear(DateUtil.date()).getTime();
        }
        //没有结束时间，默认结束时间为12月31日
        if (end == 0) {
            end = DateUtil.endOfYear(DateUtil.date()).getTime();
        }
        Map<String, BigDecimal> map = new LinkedHashMap();

        end = DateUtil.endOfQuarter(DateUtil.date(end)).getTime();
        for (long i = DateUtil.endOfQuarter(DateUtil.date(start)).getTime(); i <= end; i = DateUtil.offsetMonth(DateUtil.date(i), 3).getTime()) {
            map.put(DateUtil.date(i) + "", new BigDecimal(0));
        }
        return map;
    }

    /**
     * 初始化按季度统计交易笔数的map
     *
     * @param start
     * @param end
     * @return
     */
    private Map<String, Integer> initMonthCountMap(long start, long end) {

        //如果没传开始时间，默认开始时间为今年1月1日
        if (start == 0) {
            start = DateUtil.beginOfYear(DateUtil.date()).getTime();
        }
        //没有结束时间，默认结束时间为12月31日
        if (end == 0) {
            end = DateUtil.endOfYear(DateUtil.date()).getTime();
        }
        Map<String, Integer> map = new LinkedHashMap();
        end = DateUtil.endOfQuarter(DateUtil.date(end)).getTime();
        for (long i = DateUtil.endOfQuarter(DateUtil.date(start)).getTime(); i <= end; i = DateUtil.offsetMonth(DateUtil.date(i), 3).getTime()) {
            map.put(DateUtil.date(i) + "", 0);
        }

        return map;
    }


    /**
     * 获取默认开始时间,默认当前季度开始的时候
     *
     * @param start 开始时间
     * @return
     */
    private static long getDefaultStart(long start) {
        //如果没传时间进来   默认开始时间是今天开始时间
        if (start == 0) {
            start = System.currentTimeMillis();
            start = DateUtil.beginOfQuarter(DateUtil.date(start)).getTime();
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
            end = DateUtil.endOfYear(DateUtil.date()).getTime();
        }

        return end;
    }
}
