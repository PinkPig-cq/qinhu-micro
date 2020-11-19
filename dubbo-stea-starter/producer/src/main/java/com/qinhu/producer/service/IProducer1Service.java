package com.qinhu.producer.service;

import com.qinhu.api.Db;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-13 15:40
 **/
public interface IProducer1Service {

    /**
     * xx
     *
     * @param addMoney x
     * @return
     */
    Db sagaOne(BigDecimal addMoney);

    /**
     * saga的补偿行为
     *
     * @param addMoney x
     */
    void compentSagaOne(BigDecimal addMoney);
}
