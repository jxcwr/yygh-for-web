package com.atguigu.yygh.sms.service;

import com.atguigu.yygh.vo.msm.MsmVo;

public interface SendSMSService {
    boolean send(String phone);

    void sendMessage(MsmVo msmVo);
}
