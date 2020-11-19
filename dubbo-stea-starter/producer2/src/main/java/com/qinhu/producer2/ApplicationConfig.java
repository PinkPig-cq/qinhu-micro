package com.qinhu.producer2;

import io.seata.saga.engine.StateMachineConfig;
import io.seata.saga.engine.StateMachineEngine;
import io.seata.saga.engine.config.DbStateMachineConfig;
import io.seata.saga.engine.impl.DefaultStateMachineConfig;
import io.seata.saga.engine.impl.ProcessCtrlStateMachineEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-19 15:59
 **/
@Configuration
public class ApplicationConfig {

    @Bean
    public StateMachineEngine stateMachineEngine(StateMachineConfig stateMachineConfig){
        ProcessCtrlStateMachineEngine stateMachineEngine = new ProcessCtrlStateMachineEngine();
        stateMachineEngine.setStateMachineConfig(stateMachineConfig);
        return stateMachineEngine;
    }

    @Bean
    public StateMachineConfig stateMachineConfig(){
        return new DefaultStateMachineConfig();
    }
}
