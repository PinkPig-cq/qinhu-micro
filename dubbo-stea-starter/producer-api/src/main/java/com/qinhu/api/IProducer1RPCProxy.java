package com.qinhu.api;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-16 10:32
 **/
public interface IProducer1RPCProxy {
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
