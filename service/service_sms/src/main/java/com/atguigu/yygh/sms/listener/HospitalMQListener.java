package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.mq.constantClass.MqConst;
import com.atguigu.yygh.mq.service.RabbitService;
import com.atguigu.yygh.sms.service.SendSMSService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月07日 11:32
 */
@Component
public class HospitalMQListener {

    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private SendSMSService sendSMSService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_SMS_ITEM),//创建队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_SMS),//创建交换机
                    key = MqConst.ROUTING_SMS_ITEM//路由key


            )
    })
    public void consume(MsmVo msmVo, Message message, Channel channel) {

        sendSMSService.sendMessage(msmVo);
    }
}
