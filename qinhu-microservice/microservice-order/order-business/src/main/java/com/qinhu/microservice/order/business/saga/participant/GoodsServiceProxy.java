package com.qinhu.microservice.order.business.saga.participant;

import com.qinhu.microservice.good.api.cancel.GoodServiceCancel;
import com.qinhu.microservice.good.api.command.AddStoreCommand;
import com.qinhu.microservice.good.api.command.ReduceStoreCommand;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;

/**
 * @description: 商品服务的代理对象,此处主要是来维护各个Saga的命令发送目的地信息
 *              为什么要用这个代理？
 *                  1.基于命令模式存在难看出业务间协助关系的问题,而代理可以直接申明式的维护关系,不关注细节实现。
 *                  2.建议看看《微服务设计模式》一书Saga章节
 * @author: qh
 * @create: 2020-07-09 23:22
 **/
public class GoodsServiceProxy {

    /**
     * 消息描述: 用于确定ReduceStoreCommand命令发送的队列
     * Good模块 接收扣减库存的 EndPointCommand信息
     *
     */
    public static final CommandEndpoint<ReduceStoreCommand> reduceStoreCommandEndpoint = CommandEndpointBuilder
            //接收的命令类型
            .forCommand(ReduceStoreCommand.class)
            //接收命令队列的名字
            .withChannel(GoodServiceCancel.reduceStoreCancel)
            //响应类型  可以定义多个如  成功/失败同存在 Saga根据Reply不通处理
            .withReply(Success.class)
            .build();

    /**
     * Good模块 接收扣减库存的补偿 EndPointCommand信息
     */
    public static final CommandEndpoint<AddStoreCommand> addStoreCommandEndPoint = CommandEndpointBuilder
            .forCommand(AddStoreCommand.class)
            .withChannel(GoodServiceCancel.addStoreCancel)
            .withReply(Success.class)
            .build();
}
