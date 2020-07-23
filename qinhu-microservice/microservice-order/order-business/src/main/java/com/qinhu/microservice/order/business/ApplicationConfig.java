package com.qinhu.microservice.order.business;

import com.qinhu.microservice.order.business.domain.eventpublisher.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.messagehandlers.OrderServiceEventMessageHandlersConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
@EnableAspectJAutoProxy
@EnableTransactionManagement
@Import({
        TramEventsPublisherConfiguration.class,//默认DomainEventPublisher注入
        TramJdbcKafkaConfiguration.class,//看源码:jdbc连接DB用CDC模式传输到kafka
        OrderServiceEventMessageHandlersConfiguration.class, //领域事件消费者 配置类
        TramEventSubscriberConfiguration.class,
        SagaOrchestratorConfiguration.class
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



     /**
     * 系统性能监控配置  <千分尺>  Micrometer
     * https://micrometer.io/docs
     *
     * @return 自定义Micrometer注册器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> registryCustomizer() {

        return new MeterRegistryCustomizer<MeterRegistry>() {
            @Override
            public void customize(MeterRegistry registry) {
                //自定义的注册器,在这里只是指定了这个监控的tag
                registry.config().commonTags("MeterServiceName","microservice-order");
            }
        };
    }

}
