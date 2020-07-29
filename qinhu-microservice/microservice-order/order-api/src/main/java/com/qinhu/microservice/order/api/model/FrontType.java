package com.qinhu.microservice.order.api.model;

import lombok.Getter;

/**
 * @description: 平台类型
 * @author: qh
 * @create: 2020-07-25 09:38
 **/
@Getter
public enum FrontType {

    /**
     * 收银端
     */
    CASHIER(1, "CASHIER"),
    /**
     * 网上商城
     */
    ESHOP(2, "ESHOPPING"),
    /**
     * 展览
     */
    EXHIBITION(3, "EXHIBITION");


    String name;
    int code;

    FrontType(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
