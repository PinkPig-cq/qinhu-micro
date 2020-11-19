package com.qinhu.producer.service;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-13 15:40
 **/
public interface IProducerService {

    /**
     * xx
     * @param addMoney x
     * @return
     */
    ProducerServiceImpl.Db saga(BigDecimal addMoney);

    /**
     * saga的补偿行为
     * @param addMoney x
     */
    void compentSaga(BigDecimal addMoney);
}
