package com.qinhu.microservice.good.business.consumer;

import com.qinhu.microservice.good.api.cancel.GoodServiceCancel;
import com.qinhu.microservice.good.api.command.AddStoreCommand;
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.stereotype.Component;

/**
 * @description: 添加Good库存的Handlers
 * @author: qh
 * @create: 2020-07-10 14:21
 **/
@Component
public class AddStoreCommandHandlers {

    public CommandHandlers addStoreCommandHandlers(){
        return SagaCommandHandlersBuilder
                .fromChannel(GoodServiceCancel.addStoreCancel)
                .onMessage(AddStoreCommand.class,this::addStoreCommandHandler)
                .build();
    }

    private Message addStoreCommandHandler(CommandMessage<AddStoreCommand> cm){

        //todo 补偿扣减的库存
        System.out.println("扣减库存的补偿事件");
        return CommandHandlerReplyBuilder.withSuccess();
    }
}
