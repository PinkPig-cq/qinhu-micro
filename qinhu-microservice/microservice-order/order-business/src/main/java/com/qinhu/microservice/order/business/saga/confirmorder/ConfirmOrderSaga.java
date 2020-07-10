package com.qinhu.microservice.order.business.saga.confirmorder;

import com.qinhu.microservice.order.business.saga.participant.GoodsServiceProxy;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @description: 确定订单支付Saga
 * @author: qh
 * @create: 2020-07-09 18:43
 * @Param ConfirmOrderSagaState 当前Saga流程的上下文,会从第一个参与者一直向下传递
 **/
@Data
@Component
public class ConfirmOrderSaga implements SimpleSaga<ConfirmOrderSagaState> {

    /**
     * Saga定义描述
     */
    private final SagaDefinition<ConfirmOrderSagaState> confirmOrderSagaState;

    /**
     * 构造Saga描述定义,主要看step()方法:每个step为一个Saga业务组成
     * invokeParticipant():定义这个Saga的Mq通信协议.
     *
     * @参数1:
     * @参数2:命令传输的队列管道信息{@link CommandEndpoint}.
     * @参数3:传输的命令 withCompensation():定义补偿行为,参数同invokeParticipant.
     * .onReply():定义invokeParticipant()响应对象的处理
     */
    public ConfirmOrderSaga() {
        this.confirmOrderSagaState = this.step()
                //向reduceStoreCommandEndpoint队列中发送ReduceStoreCommand命令
                .invokeParticipant(GoodsServiceProxy.reduceStoreCommandEndpoint,
                        ConfirmOrderSagaState::reduceStoreCommand)
                //ReduceStoreCommandDispatcher处理命令后的响应
                .onReply(Success.class, (state, success) -> {
                    System.out.println("Good服务扣减扣除成功了!");
                })
                //补偿命令  添加库存  由下一个Saga处理失败时调用
                .withCompensation(GoodsServiceProxy.addStoreCommandEndPoint,
                        ConfirmOrderSagaState::addStoreCommand)
                .build();
    }

    /**
     * Eventuate Tram中定义的一种描述规范:DSL
     *
     * @return ConfirmOrderSagaState这个在状态机的DSL描述
     */
    @Override
    public SagaDefinition<ConfirmOrderSagaState> getSagaDefinition() {
        return this.confirmOrderSagaState;
    }
}
