package com.qinhu.microservice.good.business.consumer;

import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @description: 当前应用全局配置
 * @author: qh
 * @create: 2020-07-05 23:08
 **/
@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@Import({
        TramEventsPublisherConfiguration.class,//默认DomainEventPublisher注入
        TramJdbcKafkaConfiguration.class,//看源码:jdbc连接DB用CDC模式传输到kafka
        TramEventSubscriberConfiguration.class,
        SagaOrchestratorConfiguration.class
})
public class ApplicationConfig {

}
