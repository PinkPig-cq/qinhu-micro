package com.qinhu.microservice.good.business.consumer;

import com.qinhu.microservice.good.api.command.ReduceStoreCommand;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @description: Good命令消费端配置
 * @author: qh
 * @create: 2020-07-10 11:11
 **/
@Configuration
@ComponentScan
@Import({SagaParticipantConfiguration.class})
public class GoodCommandHandlersConfigure {

    /**
     * 定义处理{@link ReduceStoreCommand}命令的协调器
     * 这个协调器的作用:
     *          1.调用{@link CommandHandlers}消费消息
     *          2.响应SagaManager的监听事件
     * @param sagaCommandDispatcherFactory 默认sagaCommandDispatcher工厂{@link SagaParticipantConfiguration}
     * @return reduceStoreCommandHandlerDispatcher
     */
    @Bean
    public SagaCommandDispatcher reduceStoreCommandHandlerDispatcher(
            final SagaCommandDispatcherFactory sagaCommandDispatcherFactory,
            final ReduceStoreCommandHandlers reduceStoreCommandHandlers) {

        return sagaCommandDispatcherFactory
                .make("com.qinhu.microservice.good.business.consumer.GoodCommandHandlersConfigure.reduceStoreCommandHandlerDispatcher",
                        reduceStoreCommandHandlers.reduceStoreCommandHandlers());

    }

    /**
     * 上面扣减库存的补偿命令监听
     * @param sagaCommandDispatcherFactory  同上
     * @param addStoreCommandHandlers 同上
     * @return addStoreCommandHandlersDispatcher
     */
    @Bean
    public SagaCommandDispatcher addStoreCommandHandlersDispatcher(
            final SagaCommandDispatcherFactory sagaCommandDispatcherFactory,
            final AddStoreCommandHandlers addStoreCommandHandlers){
        return sagaCommandDispatcherFactory
                .make("com.qinhu.microservice.good.business.consumer.GoodCommandHandlersConfigure.addStoreCommandHandlersDispatcher",
                        addStoreCommandHandlers.addStoreCommandHandlers());
    }
}
