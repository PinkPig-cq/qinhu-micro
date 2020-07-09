package com.qinhu.microservice.order.business.repository;

import com.qinhu.microservice.order.business.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @description: 订单仓储
 * @author: qh
 * @create: 2020-06-11 16:06
 **/
@Repository
public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

}
