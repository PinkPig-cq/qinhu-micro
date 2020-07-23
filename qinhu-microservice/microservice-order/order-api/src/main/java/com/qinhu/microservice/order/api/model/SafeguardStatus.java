package com.qinhu.microservice.order.api.model;

/**
 * 售后状态
 * @author 秦虎
 */
public enum SafeguardStatus {
    /**
     * 全部
     */
    ALL,
    /**
     * 申请退款
     */
    APPLY,
    /**
     * 审核通过
     */
    AUDIT_YES,
    /**
     * 审核不通过
     */
    AUDIT_NO,
    /**
     * 部分入库
     */
    STORAGE_PORTION,
    /**
     * 取消申请售后
     */
    APPLY_CANCEL,
    /**
     * 退款中
     */
    REFUND,

    /**
     * 退款失败
     */
    REFUND_FAIL,
    /**
     * 售后完成
     */
    REFUND_COMPLETE
}
