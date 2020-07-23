package com.qinhu.microservice.order.api.model;

/**
 *
 *
 * @author 秦虎*/
public enum OrderPayCallbackExType {

    /**
     * 申请退款时  未响应
     */
    APPLY_REFUND_EXCEPTION,
    /**
     * 唤起支付时  未回调
     */
    PAY_EXCEPTION
}
