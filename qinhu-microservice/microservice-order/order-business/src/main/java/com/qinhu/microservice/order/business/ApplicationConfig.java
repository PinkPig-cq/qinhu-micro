package com.qinhu.microservice.order.business;

import com.qinhu.microservice.order.business.domain.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.messagehandlers.OrderServiceEventMessageHandlersConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;

/**
 * @description: 当前应用全局配置
 * @author: qh
 * @create: 2020-07-05 23:08
 **/
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@Import({
        TramEventsPublisherConfiguration.class,//默认DomainEventPublisher注入
        TramJdbcKafkaConfiguration.class,//看源码:jdbc连接DB用CDC模式传输到kafka
        OrderServiceEventMessageHandlersConfiguration.class, //领域事件消费者 配置类
        TramEventSubscriberConfiguration.class
})
public class ApplicationConfig {

    /**
     * 将订单领域事件  发布者注入Bean
     *
     * @param domainEventPublisher 来自TramEventsPublisherConfiguration默认注入
     * @return
     */
    @Bean
    public OrderDomainEventPublisher orderDomainEventPublisher(DomainEventPublisher domainEventPublisher) {
        return new OrderDomainEventPublisher(domainEventPublisher);
    }


}
