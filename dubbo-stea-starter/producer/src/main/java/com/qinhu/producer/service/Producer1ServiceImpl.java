package com.qinhu.producer.service;

import com.qinhu.api.Db;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-13 15:40
 **/
@Component
public class Producer1ServiceImpl implements IProducer1Service {

    private static ThreadLocal<Db> db = new ThreadLocal<>();

    public Producer1ServiceImpl() {
        db.set(new Db("分布式事务", new BigDecimal(100)));
    }

    @Override
    public Db sagaOne(BigDecimal addMoney) {

        //分布式事务,有对持久对象操作才有事务
        //补偿策略   ===>   n+1补偿n(第二个事务失败,补偿前一个事务)
        // 某天你用我手机逛淘宝下单的时候,发现我淘宝默认地址是一个女生的(这女生还不是家人)...
        Producer1ServiceImpl.db.get().getPrice().add(addMoney);
        return db.get();
    }

    @Override
    public void compentSagaOne(BigDecimal addMoney) {

        //上面方法的补偿
        db.get().getPrice().subtract(addMoney);
    }


}
