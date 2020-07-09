package com.qinhu.microservice.order.api.model.eventload;

import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;


/**
 * @description: 创建订单事件负载
 * @author: qh
 * @create: 2020-07-06 10:39
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OrderCreatedEventLoad implements Serializable {

    private static final long serialVersionUID = 7933014503801769874L;

    /**
     * 创建订单参数
     */
    @NonNull
    CreateOrderQuery createOrderQuery;

    /**
     * 订单号
     */
    @NonNull
    private String orderNo;

}
