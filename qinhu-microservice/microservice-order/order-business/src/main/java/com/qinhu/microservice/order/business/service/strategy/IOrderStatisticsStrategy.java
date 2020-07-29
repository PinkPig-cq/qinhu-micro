package com.qinhu.microservice.order.business.service.strategy;


import com.qinhu.microservice.order.business.domain.Order;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description: 订单统计策略模式
 * @author: qh
 * @create: 2020-01-02 15:20
 **/
public interface IOrderStatisticsStrategy {

    /**
     * 根据支付方式统计交易金额
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, BigDecimal> statisticsMoneyByPayMenthod(String shopName, long start, long end);

    /**
     * 根据支付方式统计交易笔数
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, Integer> statisticsCountByPayMenthod(String shopName, long start, long end);

    /**
     * 统计交易总金额
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return 金额
     */
    BigDecimal statisticsTradeMoney(String shopName, long start, long end);

    /**
     * 统计交易总笔数
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return 笔数
     */
    Integer statisticsTradeCount(String shopName, long start, long end);

    /**
     * 统计退款总金额
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return 金额
     */
    BigDecimal statisticsRefundMoney(String shopName, long start, long end);

    /**
     * 统计退款总笔数
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return 笔数
     */
    Integer statisticsRefundCount(String shopName, long start, long end);

    /**
     * 统计交易金额折线图 数据
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, BigDecimal> statisticsTradeMoneyChart(String shopName, long start, long end);

    /**
     * 统计交易笔数折线图 数据
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, Integer> statisticsTradeCountChart(String shopName, long start, long end);

    /**
     * 统计退款笔数折线图 数据
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, Integer> statisticsRefundCountChart(String shopName, long start, long end);

    /**
     * 统计退款金额折线图 数据
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @return
     */
    Map<String, BigDecimal> statisticsRefundMoneyChart(String shopName, long start, long end);

    /**
     * 获取当前统计的交易记录
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @param payment  支付方式
     * @return
     */
    List<Order> statisticsList(String shopName, long start, long end, int payment);
}
