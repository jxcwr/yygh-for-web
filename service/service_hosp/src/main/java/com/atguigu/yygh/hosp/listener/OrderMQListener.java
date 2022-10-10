package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.mq.constantClass.MqConst;
import com.atguigu.yygh.mq.service.RabbitService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
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
 * @date 2022年10月06日 22:05
 */
@Component
public class OrderMQListener {

    @Autowired
    private ScheduleService scheduleService;

    //注入公共模块的service
    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    /*
                    * 创建了交换机和队列
                    * */
                    value = @Queue(name = MqConst.QUEUE_ORDER),//创建队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER),//创建交换机
                    key = MqConst.ROUTING_ORDER
            )
    })
    public void consume(OrderMqVo orderMqVo, Message message, Channel channel) {

        //排班id
        String scheduleId = orderMqVo.getScheduleId();
        //剩余课预约数
        Integer availableNumber = orderMqVo.getAvailableNumber();
        Boolean flag = scheduleService.updateAvailableNumber(scheduleId, availableNumber);

        MsmVo msmVo = orderMqVo.getMsmVo();
        if (flag && msmVo != null) {
            //给就诊人发送预约成功的短信
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS, MqConst.ROUTING_SMS_ITEM, msmVo);
        }
    }
}
