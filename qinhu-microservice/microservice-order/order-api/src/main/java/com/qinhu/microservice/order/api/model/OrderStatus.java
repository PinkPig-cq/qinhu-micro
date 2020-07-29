package com.qinhu.microservice.order.api.model;

import com.qinhu.common.core.exception.BusinessExceptionEnum;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单状态
 * @author: qh
 * @create: 2020-07-05 18:24
 **/
public enum OrderStatus {


    /**
     * 待支付
     */
    WAIT_PAY(1, "WAIT_PAY"),
    /**
     * 支付中
     */
    CONFIRM(2, "CONFIRM"),
    /**
     * 待发货
     */
    WAIT_DELIVERING(3, "WAIT_DELIVERING"),
    /**
     * 待收货
     */
    WAIT_RECEIVE(4, "WAIT_RECEIVE"),
    /*
     * 待评价
     */
    WAIT_COMMENT(5, "WAIT_COMMENT"),

    /**
     * 挂单
     */
    ENTRY_ORDER(100, "ENTRY_ORDER"),
    /**
     * 取单
     */
    EXIT_ORDER(101, "EXIT_ORDER"),

    /**
     * 已完成
     */
    COMPLETE(200, "COMPLETE"),
    /**
     * 退款中
     */
    REFUNDING(-1, "REFUNDING"),
    /**
     * 已退款
     */
    REFUND(-2, "REFUND"),
    /**
     * 已取消
     */
    CANCELLED(0, "CANCELLED");


    private int key;
    private String name;

    OrderStatus(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取某个订单状态之后的状态
     *
     * @param status 订单状态节点
     * @return 状态集合
     */
    public List<OrderStatus> afterStatus(OrderStatus status) {

        Assert.notNull(status, "afterStatus()参数不能为null!");

        List<OrderStatus> rts = new ArrayList<>(OrderStatus.values().length);
        OrderStatus[] values = OrderStatus.values();
        int targetKey = status.getKey();
        for (OrderStatus orderStatus : values) {
            if (orderStatus.getKey() > targetKey) {
                rts.add(orderStatus);
            }
        }
        return rts;
    }

    /**
     * 获取某个订单状态之前的状态
     *
     * @param status 订单状态节点
     * @return 状态集合
     */
    public List<OrderStatus> beforeStatus(OrderStatus status) {

        Assert.notNull(status, "afterStatus()参数不能为null!");

        List<OrderStatus> rts = new ArrayList<>(OrderStatus.values().length);
        OrderStatus[] values = OrderStatus.values();
        int targetKey = status.getKey();
        for (OrderStatus orderStatus : values) {
            if (orderStatus.getKey() < targetKey) {
                rts.add(orderStatus);
            }
        }
        return rts;
    }


    /**
     * 根据名字初始化订单状态枚举
     * @param name 名字
     * @return OrderStatus
     */
    public static OrderStatus initByName(String name) {
        BusinessExceptionEnum.LICENCE_NOT_FOUND.assertNotEmpty(name);
        OrderStatus[] values = OrderStatus.values();
        for (OrderStatus orderStatus : values) {
            if (name.equals(orderStatus.getName())) {
                return orderStatus;
            }
        }
        throw new RuntimeException("没有匹配的订单状态!");
    }

}
