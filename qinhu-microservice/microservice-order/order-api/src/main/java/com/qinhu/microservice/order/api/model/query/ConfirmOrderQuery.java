package com.qinhu.microservice.order.api.model.query;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @description: 确认订单
 * @author: qh
 * @create: 2020-07-09 10:23
 **/
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ConfirmOrderQuery {

    /**
     * 收货地址  todo 值对象
     */
    private String address;

    /**
     * 订单号
     */
    @NonNull
    private String orderNo;

    /**
     * 收货人信息
     */
    @NonNull
    private String consignee;

}
