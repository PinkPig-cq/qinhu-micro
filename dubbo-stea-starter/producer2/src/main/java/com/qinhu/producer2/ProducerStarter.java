package com.qinhu.producer2;

import com.qinhu.producer2.service.IProducer2Service;
import io.seata.saga.engine.StateMachineEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-19 15:55
 **/
@Component
public class ProducerStarter implements ApplicationRunner {

    @Autowired
    IProducer2Service iProducer2Service;
    @Autowired
    StateMachineEngine stateMachineEngine;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Map<String,Object> startParams = new HashMap<>(1);
        startParams.put("addMoney",new BigDecimal(10));
        stateMachineEngine.startWithBusinessKey("sagaStart",
                null, "key", startParams);

    }
}
