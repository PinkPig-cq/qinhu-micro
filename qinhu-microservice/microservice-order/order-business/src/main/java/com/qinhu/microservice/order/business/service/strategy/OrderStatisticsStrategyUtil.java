package com.qinhu.microservice.order.business.service.strategy;


import com.qinhu.microservice.order.api.service.IOrderServiceRpc;
import com.qinhu.microservice.order.business.repository.OrderRepository;

/**
 * @description: 订单统计策略工具类
 * @author: qh
 * @create: 2020-01-03 14:53
 **/
public class OrderStatisticsStrategyUtil {

    /**
     * 获取策略
     *
     * @param strategy 策略选择  1:每月  2:每季度  3:每年  4:自定义  其他:每天
     * @param orderRepository 订单业务类
     * @return
     */
    public static IOrderStatisticsStrategy getStrategy(int strategy, OrderRepository orderRepository) {

        IOrderStatisticsStrategy iOrderStatisticsStrategy;

        switch (strategy) {
            case 1:
                iOrderStatisticsStrategy = new OrderStatisticsStrategyMonth(orderRepository);
                break;
            case 2:
                iOrderStatisticsStrategy = new OrderStatisticsStrategyQuarter(orderRepository);
                break;
            case 3:
                iOrderStatisticsStrategy = new OrderStatisticsStrategyYear(orderRepository);
                break;
            case 4:
                iOrderStatisticsStrategy = new OrderStatisticsStrategyCustom(orderRepository);
                break;
            default:
                iOrderStatisticsStrategy = new OrderStatisticsStrategyDay(orderRepository);
                break;
        }

        return iOrderStatisticsStrategy;
    }
}
