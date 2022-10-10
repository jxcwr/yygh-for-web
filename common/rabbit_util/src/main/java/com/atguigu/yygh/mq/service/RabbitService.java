package com.atguigu.yygh.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月06日 19:43
 */
@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange, String routingKey, Object message) {

        //发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
