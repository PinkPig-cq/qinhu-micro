package com.qinhu.producer2;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: qh
 * @create: 2020-11-16 10:27
 **/
@EnableDubbo
@SpringBootApplication
public class Producer2Application {

    public static void main(String[] args) {
        SpringApplication.run(Producer2Application.class, args);
    }

}
