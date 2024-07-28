package com.wh.dubbbo.demo.mg;

import io.seata.config.springcloud.EnableSeataSpringConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@EnableSeataSpringConfig
public class MgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MgApplication.class, args);
    }
}
