package com.atguigu.yygh.sms.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.sms.service.SendSMSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月27日 18:06
 */
@Api(tags = "短信验证码")
@RestController
@RequestMapping("user/sms")
public class SendSMSController {

    @Autowired
    private SendSMSService sendSMSService;

    @ApiOperation("给用户的手机号发送验证码")
    @PostMapping("/send/{phone}")
    public R sendSMS(@PathVariable("phone") String phone) {

        boolean flag = sendSMSService.send(phone);

        if (flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}
