package com.atguigu.yygh.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月06日 19:47
 */
@SpringBootConfiguration
public class RabbitConfig {

    //作用：将发送到RabbitMq中的pojo对象自动转化话json格式存储，从RabbitMQ消费消息时，将json格式自动转化为pojo对象
    @Bean
    public MessageConverter getMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }
}
