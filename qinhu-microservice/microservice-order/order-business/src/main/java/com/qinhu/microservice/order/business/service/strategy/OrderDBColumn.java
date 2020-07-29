package com.qinhu.microservice.order.business.service.strategy;

/**
 * @description: 订单数据库字段名
 * @author: qh
 * @create: 2020-01-02 16:08
 **/
public class OrderDBColumn {

    /**
     * 数据库支付状态字段名 YES为所有已支付订单
     */
    public static final String PAY_STATUS = "payStatus";

    /**
     * 数据库订单状态字段名 feild2维护了退款状态
     */
    public static final String ORDER_STATUS = "orderStatus";
}
