package com.qinhu.producer2;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.saga.engine.StateMachineConfig;
import io.seata.saga.engine.StateMachineEngine;
import io.seata.saga.engine.impl.DefaultStateMachineConfig;
import io.seata.saga.engine.impl.ProcessCtrlStateMachineEngine;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-19 15:59
 **/
@Configuration
@EnableAutoConfiguration
public class ApplicationConfig {

    @Bean
    public StateMachineEngine stateMachineEngine(StateMachineConfig stateMachineConfig) {
        ProcessCtrlStateMachineEngine stateMachineEngine = new ProcessCtrlStateMachineEngine();
        stateMachineEngine.setStateMachineConfig(stateMachineConfig);
        return stateMachineEngine;
    }

    @Bean
    public StateMachineConfig stateMachineConfig() throws MalformedURLException {
        DefaultStateMachineConfig stateMachineConfig = new DefaultStateMachineConfig();

        Resource[] resources = new Resource[]{
          new FileUrlResource("statelang/*.json")
        };

        stateMachineConfig.setResources(resources);
        return stateMachineConfig;
    }

    @Bean
    public DataSource dataSource(){


        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/seata?characterEncoding=UTF-8");
        return dataSource;
    }
}
