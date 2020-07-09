package com.qinhu.microservice.order.business.messagehandlers;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 消息Sender配置类, 配置接收kafka的传输类, 同rabbitmq的rabbitTemplate
 * @author: qh
 * @create: 2020-07-05 23:59
 **/
@Configuration
public class OrderServiceEventMessageHandlersConfiguration {


    /**
     * 领域事件消费者
     * @return
     */
    @Bean
    public OrderServiceEventConsumer orderServiceEventConsumer() {
        return new OrderServiceEventConsumer();
    }

    /**
     * 配置当前应用的DomainEventDispatcher具体消费哪些领域事件
     * 可以是其他上下文的领域事件  --> 所以Event应该放在调用依赖层
     *
     * @param domainEventDispatcherFactory 领域事件调度工厂
     * @return
     */
    @Bean
    public DomainEventDispatcher domainEventDispatcher(DomainEventDispatcherFactory domainEventDispatcherFactory
            , OrderServiceEventConsumer orderServiceEventConsumer) {

        //工厂方法 : eventDispatcherId 唯一标识,DomainEventHandlers 具体消息处理器
        //这里DomainEventHandlers 用于处理Order产生的领域事件
        return domainEventDispatcherFactory.make("OrderServiceEvent",
                orderServiceEventConsumer.domainEventHandlers());
    }

}
