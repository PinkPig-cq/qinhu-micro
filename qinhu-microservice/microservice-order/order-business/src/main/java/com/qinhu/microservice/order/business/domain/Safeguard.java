package com.qinhu.microservice.order.business.domain;

import com.qinhu.microservice.order.api.model.PaymentName;
import com.qinhu.microservice.order.api.model.SafeguardStatus;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * <p>
 * 维权订单  退款订单
 * </p>
 *
 * @author qinhu
 * @since 2020-02-10
 */
@Data
@Entity
public class Safeguard  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 原订单号
     */
    private String orderNo;
    /**
     * 退款订单号
     */
    private String refundNo;
    /**
     * 商铺id
     */
    private Long shopId;
    /**
     * 退款类型 0.仅钱 1.钱货
     */
    private Integer refundType;
    /**
     * 退款进度状态
     */
    @Enumerated(EnumType.STRING)
    private SafeguardStatus status;
    /**
     * 退款原因
     */
    private String reason;
    /**
     * 原支付方式
     */
    @Enumerated(EnumType.STRING)
    private PaymentName paymentMethodName;
    /**
     * 退款金额
     */
    private Double amount;
    /**
     * 前端平台
     */
    private String front;
    /**
     * 凭据图片地址  用","隔开
     */
    private String evidenceUrl;

    private String goods;

    private Long createTime;

    private Long updateTime;
    /**
     * 备注
     */
    private String remake;
    /**
     * 物流号
     */
    private String logisticsNo;

}
