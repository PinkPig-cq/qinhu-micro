package com.qinhu.microservice.order.api.model.query;

import com.qinhu.microservice.order.api.model.FrontType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @description: 创建订单请求
 * @author: qh
 * @create: 2020-07-06 10:39
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CreateOrderQuery {

    /**
     * 买家id
     */
    @NonNull
    private Long userId;
    /**
     * 所属平台
     */
    @NonNull
    private FrontType front;
    /**
     * 收银员
     */
    private String cashier;
    /**
     * 设备号
     */
    private String deviceNo;
    /**
     * 桌子号
     */
    private String tableId;
    /**
     * 商品信息
     */
    @NonNull
    private List<OrderGoodsDetail> goodsDetails;


}
