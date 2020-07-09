package com.qinhu.microservice.order.api.event;

import com.qinhu.microservice.order.api.model.eventload.OrderCreatedEventLoad;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @description: 订单创建事件
 * @author: qh
 * @create: 2020-07-05 21:41
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OrderCreatedEvent implements OrderDomainEvent {

    @NonNull
    private OrderCreatedEventLoad orderDetail;
}
