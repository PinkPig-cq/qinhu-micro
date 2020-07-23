package com.qinhu.microservice.order.business.domain.eventpublisher;

import com.qinhu.microservice.order.api.event.OrderDomainEvent;
import com.qinhu.microservice.order.business.domain.Order;
import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;


/**
 * @description: 订单领域事件发布者
 * @author: qh
 * @create: 2020-07-05 22:10
 **/
public class OrderDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Order, OrderDomainEvent> {

    /**
     * 实现父类构造函数  创建领域事件发布者
     * @param eventPublisher
     */
    public OrderDomainEventPublisher(DomainEventPublisher eventPublisher) {
        super(eventPublisher, Order.class, Order::getId);
    }

}
