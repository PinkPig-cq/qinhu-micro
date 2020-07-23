package com.qinhu.microservice.order.business;

//import io.sentry.Sentry;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description: 订单服务启动类
 * @author: qh
 * @create: 2020-06-11 15:49
 **/
@EnableDubbo
@SpringBootApplication
public class OrderApplication {

    private static Logger logger = LoggerFactory.getLogger(OrderApplication.class);

    public static void main(String[] args) {

//        Sentry.init();
        SpringApplication.run(OrderApplication.class, args);

//        logger.error("111111111111111111111111111");
//
//        try {
//            int i =1/0;
//        }catch (Exception e){
//            logger.error("Caught exception!", e);
//        }
    }
}
