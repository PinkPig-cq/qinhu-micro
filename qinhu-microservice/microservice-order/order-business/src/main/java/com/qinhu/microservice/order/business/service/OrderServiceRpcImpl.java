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
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

            return order.toOrderVo();
        } else {
            BusinessExceptionEnum.BUSINESS_ERROR.assertNotNull(null);
            return null;
        }
    }

}
