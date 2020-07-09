package com.qinhu.microservice.order.business.messagehandlers;

import com.qinhu.microservice.order.api.event.OrderCreatedEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;

/**
 * @description: 订单消息(领域事件)处理器
 * @author: qh
 * @create: 2020-07-05 23:07
 **/
public class OrderServiceEventConsumer {

    public DomainEventHandlers domainEventHandlers() {

        //AggregateType:聚合实体类路径
        //onEvent: class<T>监听的事件类型,Consumer<DomainEventEnvelope>
        //Consumer<...> 具体业务方法可以拿到消息(message-> DomainEventEnvelope<OrderCreatedEvent> envelope)
        return DomainEventHandlersBuilder
                .forAggregateType("com.qinhu.microservice.order.business.domain.Order")
                .onEvent(OrderCreatedEvent.class, this::onOrderCreated)
                .build();
    }

    /**
     * 对应的OrderCreatedEvent事件的消费者
     *
     * @param envelope
     */
    private void onOrderCreated(DomainEventEnvelope<OrderCreatedEvent> envelope) {
        //todo 消费业务
    }
}
