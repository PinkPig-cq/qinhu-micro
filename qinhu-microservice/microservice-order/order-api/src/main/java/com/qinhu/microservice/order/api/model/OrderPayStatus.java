package com.qinhu.microservice.order.api.model;

import java.util.List;

/**
 * @description: 订单支付状态
 * @author: qh
 * @create: 2020-07-05 18:24
 **/
public enum OrderPayStatus {

    /**
     * 已支付
     */
    PAY_YES(1,"PAY_YES"),
    /**
     * 未支付
     */
    PAY_NO(0,"PAY_NO");

    private String name;
    private int key;

    OrderPayStatus(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getKey() {
        return key;
    }

}
