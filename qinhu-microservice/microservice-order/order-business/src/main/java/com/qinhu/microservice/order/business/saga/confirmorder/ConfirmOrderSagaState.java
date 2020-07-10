package com.qinhu.microservice.order.business.saga.confirmorder;

import com.qinhu.microservice.good.api.command.AddStoreCommand;
import com.qinhu.microservice.good.api.command.ReduceStoreCommand;
import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 订单确认支付Saga
 * @author: qh
 * @create: 2020-07-09 17:31
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ConfirmOrderSagaState {

    /*
     * 订单确认支付流程:
     *   1.修改订单状态为已支付/待发货
     *   2.支付订单
     *   3.商品库存扣减
     * 商品提醒发货通知
     * */

    /**
     * 原始订单号
     */
    @NonNull
    private String orderNo;

    /**
     * 订单商品总价
     */
    @NonNull
    private BigDecimal totalPrice;

    /**
     * 统一下单号
     */
    private String unifiedOrderNo;

    /**
     * 支付方式
     */
    private String paymentName;

    /**
     * 支付需要的信息 TODO 集成支付系统
     */
    private String payInfo;

    /**
     * 商品信息  减库存需要
     */
    @NonNull
    private List<OrderGoodsDetail> goodsDetails;

    /**
     * 构建商品库存扣减命令
     */
    public ReduceStoreCommand reduceStoreCommand() {
        return new ReduceStoreCommand(this.goodsDetails);
    }
    /**
     * 构建商品库存扣减命令 的补偿 添加库存
     */
    public AddStoreCommand addStoreCommand() {
        return new AddStoreCommand(this.goodsDetails);
    }
}
