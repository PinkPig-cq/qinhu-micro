package com.qinhu.producer.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-13 15:40
 **/
@Component
public class ProducerServiceImpl implements IProducerService {

    private static ThreadLocal<Db> db = new ThreadLocal<>();

    public ProducerServiceImpl() {

        db.set(new Db("分布式事务", new BigDecimal(100)));
    }

    @Override
    public Db saga(BigDecimal addMoney) {

        //分布式事务,有对持久对象操作才有事务
        //补偿策略   ===>   n+1补偿n(第二个事务失败,补偿前一个事务)
        ProducerServiceImpl.db.get().getPrice().add(addMoney);
        return db.get();
    }

    @Override
    public void compentSaga(BigDecimal addMoney) {

        //上面方法的补偿
        db.get().getPrice().subtract(addMoney);
    }

    public static class Db {

        public Db(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }

        private String name;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        private BigDecimal price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
