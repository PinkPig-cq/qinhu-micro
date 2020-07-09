package com.qinhu.microservice.order.api.event;

import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @description: 订单确认支付事件
 * @author: qh
 * @create: 2020-07-09 15:51
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OrderConfirmedEvent implements OrderDomainEvent {

    /**
     * 店铺id
     */
    @NonNull
    private Long ownerId;

    /**
     * 购买的商品信息
     */
    @NonNull
    private List<OrderGoodsDetail> goodsDetails;

    /**
     * 收货人信息
     */
    @NonNull
    private String consignee;

    /**
     * 收货地址信息
     */
    @NonNull
    private String address;
}
