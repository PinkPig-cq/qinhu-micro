package com.qinhu.microservice.order.api.model;

/**
 * @description: 支付方式枚举
 * @author: qh
 * @create: 2020-07-22 20:32
 **/
public enum PaymentName {

    /**
     * 微信
     */
    WeChatPay(1, "WeChatPay"),
    /**
     * 支付宝
     */
    AliPay(2, "AliPay"),
    /**
     * 招商银行(2)
     */
    CMBPay(3, "CMBPay");

    private String name;
    private int key;

    PaymentName(int key, String name) {
        this.key = key;
        this.name = name;
    }
}
