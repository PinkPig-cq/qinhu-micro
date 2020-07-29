package com.qinhu.microservice.order.business.service.strategy;

import com.qinhu.microservice.order.business.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: 策略  自定义统计
 * @author: qh
 * @create: 2020-01-02 23:05
 **/
public class OrderStatisticsStrategyCustom extends OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {

    public OrderStatisticsStrategyCustom(OrderRepository orderRepository) {
        super(orderRepository);
    }

    @Override
    public Map<String, BigDecimal> statisticsTradeMoneyChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, Integer> statisticsTradeCountChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, Integer> statisticsRefundCountChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> statisticsRefundMoneyChart(String shopName, long start, long end) {
        return null;
    }
}
