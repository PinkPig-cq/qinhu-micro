package com.qinhu.api;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-19 16:50
 **/
public class Db {

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