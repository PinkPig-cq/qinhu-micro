package com.qinhu.producer;

import com.qinhu.api.Db;
import com.qinhu.api.IProducer1RPCProxy;
import com.qinhu.producer.service.IProducer1Service;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-16 10:31
 **/
@DubboService(version = "1.0.1", tag = "producer1的代理")
public class Prodecuer1RPCProxyImpl implements IProducer1RPCProxy {

    @Autowired
    IProducer1Service iProducer1Service;

    @Override
    public Db sagaOne(BigDecimal addMoney) {
        return iProducer1Service.sagaOne(addMoney);
    }

    @Override
    public void compentSagaOne(BigDecimal addMoney) {
        iProducer1Service.compentSagaOne(addMoney);
    }
}
