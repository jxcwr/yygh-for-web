package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月14日 12:13
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.yygh")
public class ServiceHospMainStarter {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStarter.class, args);
    }
}
