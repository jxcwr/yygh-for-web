package com.atguigu.yygh.user;

import com.atguigu.yygh.user.prop.WechatProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月27日 11:15
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu")
@MapperScan("com.atguigu.yygh.user.mapper")
@EnableConfigurationProperties(value = WechatProperties.class)
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}