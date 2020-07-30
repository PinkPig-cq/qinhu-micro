package com.qinhu.microservice.order.api.model.query;

import com.qinhu.microservice.order.api.model.OrderPayStatus;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.PaymentName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 确认订单
 * @author: qh
 * @create: 2020-07-09 10:23
 **/
@Data
@Valid
@RequiredArgsConstructor
@NoArgsConstructor
public class ChangeOrderQuery {

    /**
     * 收货地址  todo 值对象
     */
    private String address;

    /**
     * 订单号
     */
    @NonNull
    @NotNull(message = "订单号不能为空!")
    private String orderNo;

    /**
     * todo 收货人类型
     * 收货人信息
     */
    private String consignee;

    /**
     * 订单状态
     */
    @NonNull
    @NotNull(message = "订单状态不能为空!")
    private OrderStatus orderStatus;
    /**
     * 统一下单 单号  唯一
     */
    private String unifiedOrderNo;
    /**
     * 支付方式
     */
    private PaymentName paymentMethodName;

    /**
     * 支付状态  默认为No
     */
    private OrderPayStatus payStatus = OrderPayStatus.PAY_NO;
    /**
     * 备注
     */
    private String remark;
    /**
     * 物流
     */
    private String logistics;
    /**
     * 运费
     */
    private BigDecimal freight;
    /**
     * 折扣金额
     */
    private BigDecimal discount;
}
