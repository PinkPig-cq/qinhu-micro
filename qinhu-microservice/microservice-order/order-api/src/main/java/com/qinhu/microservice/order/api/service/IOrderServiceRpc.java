package com.qinhu.microservice.order.api.service;

import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ConfirmOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;

import java.math.BigDecimal;

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
    OrderVo createOrder(final CreateOrderQuery createOrderQuery);

    /**
     * 订单确认支付
     *
     * @param confirmOrderQuery 订单支付请求对象 {@link ConfirmOrderQuery}
     * @return orderVo {@link OrderVo}
     */
    OrderVo confirm(final ConfirmOrderQuery confirmOrderQuery);
}
