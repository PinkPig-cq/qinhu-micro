package com.qinhu.microservice.good.business.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-12 11:07
 **/
@EnableDubbo
@SpringBootApplication
public class GoodApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodApplication.class, args);
    }
}
