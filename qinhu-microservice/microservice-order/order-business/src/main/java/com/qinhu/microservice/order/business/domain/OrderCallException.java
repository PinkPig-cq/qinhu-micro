package com.qinhu.microservice.order.business.domain;

import com.qinhu.microservice.order.api.model.OrderPayCallbackExType;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description: 招行调用异常对象
 * @author: qh
 * @create: 2020-01-13 15:07
 **/
@Data
@Entity
@Table(name = "order_call_exception")
public class OrderCallException {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 订单号，和订单类型一起食用
     */
    private String orderNo;
    /**
     * 退款订单号,和订单类型一起食用,只有类型为退款时有这个字段
     */
    private String refundNo;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 失败次数  累计
     */
    private Integer failCount;
    /**
     * 异常订单类型
     */
    @Enumerated(EnumType.STRING)
    private OrderPayCallbackExType orderStatus;
    /**
     * 处理状态
     */
    private Integer status;
    /**
     * 消息 可能有用
     */
    private String msg;
}
