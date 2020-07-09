package com.qinhu.microservice.order.api.model.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 订单商品没描述(快照)
 * @author: qh
 * @create: 2020-07-05 21:54
 **/
@Data
public class OrderGoodsDetail {

    /**
     * 商品id
     */
    private Long goodId;

    /**
     * 数量 默认1
     */
    private Integer num = 1;

    /**
     * 快照  购买时的价格
     */
    private BigDecimal oldPrice;

    /**
     * 快照  商品名字
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 快照  图片
     */
    private String url;

    /**
     * 商品所属店铺id
     */
    private Long ownerId;
}
