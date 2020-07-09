package com.qinhu.microservice.order.business.domain;

import com.alibaba.fastjson.JSON;
import com.qinhu.common.core.exception.BusinessExceptionEnum;
import com.qinhu.common.core.exception.CodeExceptionEnum;
import com.qinhu.common.core.until.SnowflakeIdUtil;
import com.qinhu.microservice.order.api.event.OrderConfirmedEvent;
import com.qinhu.microservice.order.api.event.OrderDomainEvent;
import com.qinhu.microservice.order.api.event.OrderCreatedEvent;
import com.qinhu.microservice.order.api.model.eventload.OrderCreatedEventLoad;
import com.qinhu.microservice.order.api.model.query.CreateOrderQuery;
import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import com.qinhu.microservice.order.api.model.OrderPayStatus;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 订单领域对象
 * @author: qh
 * @create: 2020-07-05 14:50
 **/
@Data
@Entity
@Table(name = "micro_order")
public class Order {


    public static ResultWithDomainEvents<Order, OrderDomainEvent> createOrder(final CreateOrderQuery orderDetail) {

        CodeExceptionEnum.OBJECT_NOT_EMPTY.assertCollectionNotILLEGAL(orderDetail.getGoodsDetails());

        //构建领域实体
        Order order = new Order();
        //获取唯一主键
        final String orderNo = SnowflakeIdUtil.getSnowId();
        order.setOrderNo(orderNo);
        order.setOrderStatus(OrderStatus.WAIT_PAY);
        order.setPayStatus(OrderPayStatus.PAY_NO);
        order.setGoods(JSON.toJSONString(orderDetail.getGoodsDetails()));
        order.setUserId(orderDetail.getUserId());
        order.setCreateTime(new Date());

        //构建领域事件
        final OrderCreatedEventLoad orderCreatedEventLoad = new OrderCreatedEventLoad(orderDetail, orderNo);
        final OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(orderCreatedEventLoad);
        return new ResultWithDomainEvents<>(order, orderCreatedEvent);
    }

    /**
     * 订单确认支付
     */
    public ResultWithDomainEvents<Order, OrderDomainEvent> confirmOrder() {

        CodeExceptionEnum.OBJECT_NOT_EMPTY.assertNotNull(this);

        this.setPayStatus(OrderPayStatus.PAY_YES);
        this.setOrderStatus(OrderStatus.WAIT_DELIVERING);

        //订单支付确认事件  //TODO 提醒商家发货 (订单商家 1:n)
        List<OrderGoodsDetail> goodsDetails = JSON.parseArray(this.getGoods(), OrderGoodsDetail.class);
        final List<OrderDomainEvent> orderConfirmedEvents = createConfirmEventByGood(goodsDetails);


        return new ResultWithDomainEvents<>(this, orderConfirmedEvents);

    }

    /**
     * 根据订单中商品创建确认支付事件
     *
     * @param orderGoodsDetails 订单中商品集合
     * @return 订单确认支付事件集合
     */
    private List<OrderDomainEvent> createConfirmEventByGood(List<OrderGoodsDetail> orderGoodsDetails) {
        final List<OrderDomainEvent> rts = new ArrayList<>(orderGoodsDetails.size());
        Set<Long> exitGoodOwnerId = new HashSet<>(orderGoodsDetails.size());
        orderGoodsDetails.forEach(arg -> {
            if (!exitGoodOwnerId.contains(arg.getOwnerId())) {
                exitGoodOwnerId.add(arg.getOwnerId());
                final List<OrderGoodsDetail> list = new ArrayList<>();
                list.add(arg);
                OrderDomainEvent orderConfirmedEvent = new OrderConfirmedEvent(
                        arg.getOwnerId(), list, this.orderNo, "qh");
                rts.add(orderConfirmedEvent);
            } else {
                OrderDomainEvent orderConfirmedEvent = rts.stream()
                        .filter(event -> ((OrderConfirmedEvent) event).getOwnerId().longValue() == arg.getOwnerId())
                        .findFirst()
                        .orElse(null);
                CodeExceptionEnum.OBJECT_NOT_EMPTY.assertNotNull(orderConfirmedEvent);
                ((OrderConfirmedEvent) orderConfirmedEvent).getGoodsDetails().add(arg);
            }
        });
        return rts;
    }

    public OrderVo toOrderVo() {

        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(this, orderVo);

        List<OrderGoodsDetail> orderGoodsDetails = JSON
                .parseArray(this.getGoods(), OrderGoodsDetail.class);
        orderVo.setGoodsDetails(orderGoodsDetails);

        return orderVo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(unique = true, nullable = false)
    private String orderNo;

    /**
     * 统一下单 单号  唯一
     */
    private String unifiedOrderNo;

    /**
     * 商品json
     */
    private String goods;

    /**
     * 订单状态
     */
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * 支付状态
     */
    @Column(name = "pay_status")
    @Enumerated(EnumType.STRING)
    private OrderPayStatus payStatus;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户电话
     */
    private String userPhone;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 实际需要支付金额
     */
    private BigDecimal payPrice;

    /**
     * 支付方式
     */
    private String paymentMethodName;
    /**
     * 所属平台
     */
    private String paas;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 最后更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 预留字段  目前用于存物流
     */
    private String logistics;

    /**
     * 预留字段  收银员
     */
    private String cashier;

    /**
     * 预留字段 设备号
     */
    private String deviceNo;

    /**
     * 预留字段 收货地址
     */
    private String receivePlace;

    /**
     * 商家id
     */
    private Long sellerId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 当前订单获取到的积分
     */
    private BigDecimal points;
    /**
     * 桌子号
     */
    private String tableId;

    /**
     * 支付时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date payTime;

    /**
     * 折扣金额
     */
    private BigDecimal discount;

}
