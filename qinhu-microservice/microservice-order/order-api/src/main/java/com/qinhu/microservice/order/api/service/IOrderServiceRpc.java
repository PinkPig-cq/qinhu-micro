package com.qinhu.microservice.order.api.service;

import com.qinhu.common.core.model.BaseResponse;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ChangeOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import java.util.Map;

/**
 * @description: 订单服务RPC
 * @author: qh
 * @create: 2020-07-05 15:04
 **/
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
     * @param orderNo     订单号
     * @param orderStatus 要改变的状态
     * @param condition   关键条件 <field,value> 如:卖家id
     * @return OrderVo
     */
    BaseResponse<OrderVo> changeOrderStatus(String orderNo, OrderStatus orderStatus, Map<String, Object> condition);

    /**
     * 改变订单状态 同时修改用户id
     *
     * @param orderNo     订单号
     * @param userId      用户id
     * @param orderStatus 要改变的状态
     * @param condition   关键条件 <field,value> 如:卖家id
     * @return
     */
    BaseResponse<OrderVo> changeOrderStatusByUserId(String orderNo, String userId, OrderStatus orderStatus, Map<String, Object> condition);


    /**
     * 统计一个时间段的(状态)订单总数
     *
     * @param start    起始时间
     * @param end      结束时间
     * @param status   订单类型
     * @param sellerId 商户id
     * @return
     */
    BaseResponse<Long> countOrdersByDate(Long start, Long end, OrderStatus status, String sellerId);

    /**
     * 根据条件查询
     *
     * @param condition 条件 map集合  <field,value>
     * @return
     */
    BaseResponse<OrderVo> getOrders(Map<String, Object> condition);

    /**
     * 根据用户id获取订单
     *
     * @param userId 用户id
     * @param status 订单状态
     * @return
     */
    BaseResponse<OrderVo> getOrdersByUserId(String userId, OrderStatus status);

    /**
     * 根据key删除
     *
     * @param orderNo   订单号儿
     * @param condition 删除条件
     * @return
     */
    BaseResponse<OrderVo> delOrders(String orderNo, Map<String, Object> condition);

    /**
     * 编辑订单
     *
     * @param orderQuery
     * @return
     */
    BaseResponse<OrderVo> editOrder(OrderDetailQuery orderQuery);

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @return
     */
    BaseResponse<OrderVo> cancelOrder(String orderNo);

    /**
     * 支付订单
     *
     * @param orderNo
     * @return
     */
    BaseResponse<OrderVo> payOrder(String orderNo);

    /**
     * 根据条件查询
     *
     * @param conditionJson
     * @return
     */
    BaseResponse<OrderVo> getOneOrder(Map<String, Object> conditionJson);

    /**
     * 满足条件的条数
     *
     * @param ew
     * @return
     */
    long getCount(QueryWrapper ew);

    /**
     * 分页查询
     *
     * @param pageWrapperQuery
     * @return
     */
    BaseResponse<OrderVo> page(PageWrapperQuery pageWrapperQuery);

    /**
     * 批量更新 更具rderNo
     *
     * @param ordersCancels orderNo必填
     * @return
     */
    BaseResponse<Boolean> updateBatch(List<OrderDetailQuery> ordersCancels);

    /**
     * 获取统计 头部数据
     *
     * @param iOrderStatisticsStrategy
     * @return
     */
    BaseResponse<Map> statisticsTop(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end);

    /**
     * 获取统计 折线图
     *
     * @param iOrderStatisticsStrategy
     * @return
     */
    BaseResponse<Map> statisticsByChart(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end);

    /**
     * 获取统计笔数 根据支付方式
     *
     * @param iOrderStatisticsStrategy
     * @return
     */
    BaseResponse<Map> statisticsCountPayMethod(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end);

    /**
     * 获取统计金额 根据支付方式
     *
     * @param iOrderStatisticsStrategy
     * @return
     */
    BaseResponse<Map> statisticsMoneyPayMethod(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end);

    /**
     * 获取统计下的订单
     *
     * @return
     */
    BaseResponse<Map> statisticsList(IOrderStatisticsStrategy iOrderStatisticsStrategy, String shopName, long start, long end, int payment);

    /**
     * 获取集合的复杂查询
     *
     * @param pageWrapperQuery 条件
     * @return
     */
    BaseResponse<OrderVo> getListcomplex(PageWrapperQuery pageWrapperQuery);

    /**
     * 获取时间端的聚合支付金额&现金支付的总数
     *
     * @param pageWrapperQuery 查询条件构造 {@link PageWrapperQuery}
     * @return
     */
    BaseResponse<OrderVo> statisticsAggregate(PageWrapperQuery pageWrapperQuery);

    /**
     * 统计商品售卖的金额  通过卖家id
     *
     * @param pageWrapperQuery 条件
     * @return
     */
    BaseResponse<List<StatisticsGoods>> statisticsGoodsMarketMoneyGroupSellerGood(PageWrapperQuery pageWrapperQuery);


//============================================================================================================================


    /**
     * 订单确认支付
     *
     * @param changeOrderQuery 订单支付请求对象 {@link ChangeOrderQuery}
     * @return orderVo {@link OrderVo}
     */
    OrderVo confirm(final ChangeOrderQuery changeOrderQuery);


}
