package com.qinhu.producer2.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-19 15:12
 **/
@Component
public class Producer2ServiceImpl implements IProducer2Service{

    @Override
    public BigDecimal sageTwo(final BigDecimal big) {
        return big.subtract(BigDecimal.valueOf(0));
    }
}
