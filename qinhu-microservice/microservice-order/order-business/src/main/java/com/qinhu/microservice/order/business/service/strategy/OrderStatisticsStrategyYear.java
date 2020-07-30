package com.qinhu.microservice.order.business.service.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 策略  每年统计
 * @author: qh
 * @create: 2020-01-02 18:07
 **/
@SuppressWarnings("all")
public class OrderStatisticsStrategyYear extends OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {

    public OrderStatisticsStrategyYear(OrderRepository orderRepository) {
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
    private Map<String, BigDecimal> statisticsMoneyChart(String shopName, long start, long end, Map<String, String> codition) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        Map<String, BigDecimal> map = initMonthMoneyMap(start, end);
        List<Order> list = getListOnce(shopName, start, end, codition);
        list.stream().forEach(arg -> {
            addMoney(arg.getUpdateTime().getTime(), map, arg.getPayPrice());
        });

        return sortByKey(map);
    }


    /**
     * 统计每月的笔数走向
     *
     * @return
     */
    private Map<String, Integer> statisticsCountChart(String shopName, long start, long end, Map<String, String> codition) {
        start = getDefaultStart(start);
        end = getDefaultEnd(end);
        Map<String, Integer> map = initMonthCountMap(start, end);
        List<Order> list = getListOnce(shopName, start, end, codition);
        list.stream().forEach(arg -> {
            addCount(arg.getUpdateTime().getTime(), map);
        });

        return sortByKey(map);
    }

    /**
     * 根据订单时间,判断属于哪个月份,最后更新该月份的交易笔数
     *
     * @param oder 订单时间
     * @param map  月份金额集合
     */
    private void addCount(long oder, Map<String, Integer> map) {

        Date nextDate = DateUtil.endOfMonth(DateUtil.date(oder));
        Calendar calendar = new Calendar.Builder().setInstant(nextDate).build();
        map.put(calendar.get(Calendar.MONTH) + 1 + "", map.get(calendar.get(Calendar.MONTH) + 1 + "") + 1);

    }

    /**
     * 根据订单时间,判断属于哪个月份,最后更新该月份的总金额
     *
     * @param oder   订单时间
     * @param map    月份金额集合
     * @param amount 当前订单金额
     */
    private void addMoney(long oder, Map<String, BigDecimal> map, BigDecimal amount) {

        Date nextDate = DateUtil.endOfMonth(DateUtil.date(oder));
        Calendar calendar = new Calendar.Builder().setInstant(nextDate).build();
        map.put(calendar.get(Calendar.MONTH) + 1 + "", map.get(calendar.get(Calendar.MONTH) + 1 + "").add(amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));

    }

    /**
     * 初始化按年度统计金额的map
     *
     * @param start
     * @param end
     * @return
     */
    private Map<String, BigDecimal> initMonthMoneyMap(long start, long end) {


        Map<String, BigDecimal> map = new LinkedHashMap();
        for (long i = start; i <= end; i = DateUtil.offsetMonth(DateUtil.date(i), 1).getTime()) {
            Date nextDate = DateUtil.endOfMonth(DateUtil.date(i));
            Calendar calendar = new Calendar.Builder().setInstant(nextDate).build();
            map.put(calendar.get(Calendar.MONTH) + 1 + "", new BigDecimal(0));
        }

        return map;
    }

    /**
     * 初始化按年度统计交易笔数的map
     *
     * @param start
     * @param end
     * @return
     */
    private Map<String, Integer> initMonthCountMap(long start, long end) {

        Map<String, Integer> map = new LinkedHashMap();
        for (long i = start; i <= end; i = (DateUtil.offsetMonth(DateUtil.date(i), 1)).getTime()) {
            Date nextDate = DateUtil.endOfMonth(DateUtil.date(i));
            Calendar calendar = new Calendar.Builder().setInstant(nextDate).build();
            map.put(calendar.get(Calendar.MONTH) + 1 + "", 0);
        }

        return map;
    }


    /**
     * 获取默认开始时间,默认为当前时间1月1号 0:0:0 MS
     *
     * @param start 开始时间
     * @return
     */
    private static long getDefaultStart(long start) {
        //如果没传时间进来   默认开始时间是今天开始时间
        if (start == 0) {
            start = System.currentTimeMillis();
            start = DateUtil.beginOfYear(DateUtil.date(start)).getTime();
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
            end = DateUtil.endOfYear(DateUtil.date(end)).getTime();
        }
        return end;
    }
}
