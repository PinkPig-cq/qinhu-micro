package com.qinhu.microservice.order.api.model;

import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 订单Vo
 * @author: qh
 * @create: 2020-07-06 10:06
 **/
@Data
public class OrderVo {

    private Long id;

    private String orderNo;

    private Data createTime;

    private Data payTime;

    private List<OrderGoodsDetail> goodsDetails;

    /**
     * 订单状态
     */
    private OrderStatus orderStatus;

    /**
     * 支付状态
     */
    private OrderPayStatus payStatus;
    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 实际需要支付金额
     */
    private BigDecimal payPrice;
    /**
     * 支付方式
     */
    private PaymentName paymentMethodName;

    /**
     * 所属平台
     */
    private String paas;
    /**
     * 是否为退款
     */
    private boolean isRefund;

}
