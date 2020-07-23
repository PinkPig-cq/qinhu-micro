package com.qinhu.microservice.order.business.domain;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * <p>
 * 订单自动配置类
 * </p>
 *
 * @author qinhu
 * @since 2020-03-31
 */
@Data
@Entity
public class OrdersAutoSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单自动取消时间(天)
     */
    private Integer autoCancelOrderTime;

    /**
     * 订单自动收货时间(天)
     */
    private Integer autoReceiveOrderTime;

    /**
     * 订单自动完成时间(天)
     */
    private Integer autoCompleteOrderTime;

}
