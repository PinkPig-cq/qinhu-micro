package com.qinhu.microservice.order.business;

import com.playtika.test.common.spring.EmbeddedContainersShutdownAutoConfiguration;
import com.playtika.test.mysql.EmbeddedMySQLBootstrapConfiguration;
import com.playtika.test.mysql.EmbeddedMySQLDependenciesAutoConfiguration;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import com.qinhu.microservice.order.api.service.IOrderServiceRpc;
import com.qinhu.microservice.order.business.domain.eventpublisher.OrderDomainEventPublisher;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import com.qinhu.microservice.order.business.service.OrderServiceRpcImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 订单服务测试类
 * @author: qh
 * @create: 2020-07-06 15:01
 **/
@DataJpaTest //其作用是限制Spring在扫描bean时的范围,只会选择与Spring Data JPA 相关的bean
//同时会使用虚拟DataSource代替Application有配置文件中的配置
//以前是基于Application.class 来启动测试的,这样不好,重依赖会去启动Application应用。
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {OrderRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //关闭虚拟数据源
@ContextConfiguration(classes = OrderServiceRpcImpl.class)
@Import({
        //扫描TestContainer组件的配置文件 去com.playtika.test.组件名 下找配置文件
        EmbeddedMySQLDependenciesAutoConfiguration.class,  //配置一些依赖项,如Mysql的Datasource
        EmbeddedMySQLBootstrapConfiguration.class, //Docker镜像的初始化和启动
        EmbeddedContainersShutdownAutoConfiguration.class,//解决AllContainers这个Bean扫描不进去
       // TestContainerDependenciesConfig.class, //配置EmbeddedMySQLDependenciesAutoConfiguration的依赖项datasource
})
public class OrderServiceTest {

    @Autowired
    IOrderServiceRpc orderServiceRpc;
    @MockBean
    OrderDomainEventPublisher orderDomainEventPublisher;

    @Test
    public void test() {

        CreateOrderQuery createOrderQuery = new CreateOrderQuery();
        List<OrderGoodsDetail> list = new ArrayList<>(1);
        OrderGoodsDetail orderGoodsDetail = new OrderGoodsDetail();
        orderGoodsDetail.setGoodId(1L);
        orderGoodsDetail.setDescription("这个程序Order和Good使用的事件驱动实现!");
        orderGoodsDetail.setName("《事件驱动实现》");
        orderGoodsDetail.setOldPrice(new BigDecimal(100));
        orderGoodsDetail.setNum(1);
        orderGoodsDetail.setUrl("http://127.0.0.1/group1/M0/a.png");
        orderGoodsDetail.setOwnerId(1L);
        list.add(orderGoodsDetail);
        createOrderQuery.setGoodsDetails(list);
        createOrderQuery.setUserId(1L);
        OrderVo order = orderServiceRpc.createOrder(createOrderQuery);
        System.out.println(order);

    }

}
