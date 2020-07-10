package com.qinhu.microservice.good.api.cancel;

/**
 * @description: 商品服务的mq队列消息
 * 用于维护不通的管道消息，用法和Spring cloud Stream 的管道定义差不多
 * @author: qh
 * @create: 2020-07-09 23:30
 **/
public class GoodServiceCancel {

    /**
     * 扣减库存的管道名:Saga发送扣减库存的消息,就是发到这个管道,再由Goods服务的CommandDispatcher来消费
     *
     * @tips 命名建议用相对地址加上作用名
     */
    public static final String reduceStoreCancel = "com.qinhu.microservice.good.api.cancel.GoodServiceCancel.reduceStore";

    /**
     * reduceStoreCancel队列的补偿队列名  添加库存
     */
    public static final String addStoreCancel = "com.qinhu.microservice.good.api.cancel.GoodServiceCancel.addStoreCancel";
}
