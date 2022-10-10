package com.atguigu.yygh.orders.config;

import com.atguigu.yygh.mq.constantClass.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @author jxc
 * @version 1.0
 * @description TODD在生产者一端创建交换机队列
 * @date 2022年10月06日 21:28
 */
//@SpringBootConfiguration
public class OrderConfig {

    @Bean
    public Exchange getExchange() {
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_ORDER).durable(true).build();
    }

    @Bean
    public Queue getQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_ORDER).build();
    }

    public Binding getBinding(@Qualifier("getQueue") Queue queue, @Qualifier("getExchange") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_ORDER).noargs();
    }
}
