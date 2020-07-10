package com.qinhu.microservice.good.business.consumer;

import com.qinhu.microservice.good.api.cancel.GoodServiceCancel;
import com.qinhu.microservice.good.api.command.ReduceStoreCommand;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.stereotype.Component;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

/**
 * @description: 扣减库存命令具体处理者
 * @author: qh
 * @create: 2020-07-10 13:28
 **/
@Component
public class ReduceStoreCommandHandlers {


    /**
     * 将处理消息的方法和监听的队列绑定
     * @return CommandHandlers
     */
    public CommandHandlers reduceStoreCommandHandlers() {
        return SagaCommandHandlersBuilder
                //监听队列
                .fromChannel(GoodServiceCancel.reduceStoreCancel)
                //处理Message的类型和具体处理的方法
                .onMessage(ReduceStoreCommand.class, this::reduceCommandHandler)
                .build();
    }


    /**
     * Good服务中监听到{@see GoodServiceCancel.reduceStoreCancel} 队列,
     * 拿到{@link ReduceStoreCommand} 然后处理相关业务,返回的{@link Message}对象
     *
     * @param cm ReduceStoreCommand 命令信封类
     * @return Message
     */
    private Message reduceCommandHandler(final CommandMessage<ReduceStoreCommand> cm) {

        //todo 处理消息
        System.out.println("订单商品扣减库存中。。。。。。。。");
        return withSuccess();
    }
}
