package com.qinhu.microservice.order.api.service;

import com.qinhu.common.core.model.BaseResponse;
import com.qinhu.common.core.model.WrapperQuery;
import com.qinhu.microservice.order.api.model.CommodityVoOrderCopy;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ChangeOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import org.apache.dubbo.common.utils.Page;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 订单服务RPC
 * @author: qh
 * @create: 2020-07-05 15:04
 **/
@Validated
public interface IOrderServiceRpc {

    /**
     * 创建订单
     *
     * @param createOrderQuery 订单请求对象 {@link CreateOrderQuery}
     * @return orderVo {@link OrderVo}
     */
    BaseResponse<OrderVo> createOrder(final CreateOrderQuery createOrderQuery);

    /**
     * 改变订单状态
     *
     * @param orderQuery 订单参数
     * @return OrderVo
     */
    BaseResponse<OrderVo> editOrder(final ChangeOrderQuery orderQuery);

    /**
     * 统计一个时间段的(状态)订单总数
     *
     * @param start    起始时间
     * @param end      结束时间
     * @param status   订单类型
     * @param sellerId 商户id
     * @return Long
     */
    BaseResponse<Long> countOrdersByDate(Date start, Date end, OrderStatus status, String sellerId);


    /**
     * 获取订单集合
     *
     * @param wrapperQuery 条件
     * @return List<OrderVo>
     */
    BaseResponse<List<OrderVo>> getListComplex(final WrapperQuery wrapperQuery);


    /**
     * 获取订单分页
     *
     * @param wrapperQuery 分页条件
     * @return Page<OrderVo>
     */
    BaseResponse<Page<OrderVo>> page(final WrapperQuery wrapperQuery);

    /**
     * 批量更新
     *
     * @param queryList 待更新的订单对象
     * @return bool
     */
    boolean updateBatch(@NotNull(message = "集合不能为空!") List<ChangeOrderQuery> queryList);

    /**
     * 统计顶部数据
     *
     * @param orderStatisticsStrategy 统计时间策略
     * @param shopName                查询的店铺名
     * @param start                   开始时间
     * @param end                     结束时间
     * @return map
     */
    BaseResponse<Map<String, Double>> topStatistics(final Integer orderStatisticsStrategy,
                                                    final String shopName, final Date start,
                                                    final Date end);

    /**
     * 交易金额  图表数据
     *
     * @param orderStatisticsStrategy 统计时间策略
     * @param shopName                查询的店铺名
     * @param start                   开始时间
     * @param end                     结束时间
     * @return map
     */
    BaseResponse<Map<String,Map>> tradeStatistics(final Integer orderStatisticsStrategy,
                                                                   final String shopName, final Date start,
                                                                   final Date end);

    /**
     * 统计每种支付的笔数
     *
     * @param orderStatisticsStrategy 统计时间策略
     * @param shopName                查询的店铺名
     * @param start                   开始时间
     * @param end                     结束时间
     * @return map
     */
    BaseResponse<Map<String, Integer>> countByPayMethodStatistics(final Integer orderStatisticsStrategy,
                                                                  final String shopName, final Date start,
                                                                  final Date end);

    /**
     * 统计每种支付的笔数
     *
     * @param orderStatisticsStrategy 统计时间策略
     * @param shopName                查询的店铺名
     * @param start                   开始时间
     * @param end                     结束时间
     * @return map
     */
    BaseResponse<Map<String, Number>> moneyByPayMethodStatistics(final Integer orderStatisticsStrategy,
                                                                 final String shopName, final Date start,
                                                                 final Date end);

    /**
     * 按条件统计金额变动
     *
     * @param wrapperQuery 条件
     * @return map
     */
    BaseResponse<Map<String, BigDecimal>> workingCapitalStatistiscs(final WrapperQuery wrapperQuery);

    /**
     * 统计当日 商品卖出和库存
     *
     * @param wrapperQuery 条件
     * @return List<CommodityVo>
     */
    BaseResponse<List<CommodityVoOrderCopy>> statisticsGoodsMarketMoneyGroupSellerGood(final WrapperQuery wrapperQuery);

    /**
     * 获取符合条件的订单条数
     *
     * @param wrapperQuery 条件
     * @return long
     */
    long getCount(final WrapperQuery wrapperQuery);

    /**
     * 订单确认支付
     *
     * @param changeOrderQuery 订单支付请求对象 {@link ChangeOrderQuery}
     * @return orderVo {@link OrderVo}
     */
    OrderVo confirm(final ChangeOrderQuery changeOrderQuery);

    /**
     * 根据订单号获取单个订单
     * @param orderNo 订单号
     * @return orderVo
     */
    BaseResponse<OrderVo> oneOrderByNo(String orderNo);
}
