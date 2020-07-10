package com.qinhu.microservice.order.business.service;

import com.qinhu.common.core.exception.BusinessExceptionEnum;
import com.qinhu.common.core.exception.CodeExceptionEnum;
import com.qinhu.microservice.order.api.event.OrderDomainEvent;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.ConfirmOrderQuery;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import com.qinhu.microservice.order.api.service.IOrderServiceRpc;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.domain.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSaga;
import com.qinhu.microservice.order.business.saga.confirmorder.ConfirmOrderSagaState;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.sagas.orchestration.SagaInstance;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @description: 订单PRC实现
 * @author: qh
 * @create: 2020-07-05 15:05
 **/
@Service(interfaceName = "IOrderServiceRpc", version = "0.0.1", document = "订单服务")
public class OrderServiceRpcImpl implements IOrderServiceRpc {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDomainEventPublisher domainEventPublisher;

    /**
     * Saga构建工厂
     */
    @Autowired
    SagaInstanceFactory sagaInstanceFactory;
    @Autowired
    ConfirmOrderSaga confirmOrderSaga;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo createOrder(final CreateOrderQuery createOrderQuery) {

        //领域实体入库
        final ResultWithDomainEvents<Order, OrderDomainEvent> resultWithDomainEvents = Order
                .createOrder(createOrderQuery);
        final Order order = resultWithDomainEvents.result;
        orderRepository.save(order);

        //发布事件到DB      //TODO 接收者为商品服务  预减库存
        domainEventPublisher.publish(order, resultWithDomainEvents.events);

        return order.toOrderVo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo confirm(final ConfirmOrderQuery confirmOrderQuery) {


        Optional<Order> optional = orderRepository.findOne((Specification<Order>) (root, query, criteriaBuilder) -> {
            root.get("order_no");
            return criteriaBuilder.equal(root, confirmOrderQuery.getOrderNo());
        });

        if (optional.isPresent()) {
            final Order order = optional.get();
            ResultWithDomainEvents<Order, OrderDomainEvent> confirmOrder = order.confirmOrder();
            Order updateOrder = confirmOrder.result;
            orderRepository.save(updateOrder);
            //商户发货事件通知
            domainEventPublisher.publish(order, confirmOrder.events);

            /*
             * Saga的调用
             *   1.构建ConfirmOrder的Saga状态机
             *   2.使用sagaInstanceFactory构建SagaInstance
             *   3.框架自动将SagaInstance托管给SagaManager去调用
             * */
            BigDecimal totalPrice = order.getTotalPrice();
            ConfirmOrderSagaState confirmOrderSagaState = new ConfirmOrderSagaState("000001", totalPrice,new ArrayList<>());

            sagaInstanceFactory.create(confirmOrderSaga, confirmOrderSagaState);

            return order.toOrderVo();
        } else {
            BusinessExceptionEnum.BUSINESS_ERROR.assertNotNull(null);
            return null;
        }
    }

}
