package com.atguigu.yygh.sms.service.impl;

import com.atguigu.yygh.sms.service.SendSMSService;
import com.atguigu.yygh.sms.utils.HttpUtils;
import com.atguigu.yygh.sms.utils.RandomUtil;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月27日 18:08
 */
@Service
public class SendSMSServiceImpl implements SendSMSService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean send(String phone) {

        String code = (String) redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            return true;
        }

        /**
         * 发送短信
         */

        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "550c76f79aca4e3d8ae1a4aee1e362f4";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        //手机号
        querys.put("mobile", phone);
        //验证码
        String fourBitRandom = RandomUtil.getFourBitRandom();
        querys.put("param", "code:" + fourBitRandom);

        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));

            //将验证码保存在redis中
            redisTemplate.opsForValue().set(phone, fourBitRandom, 30, TimeUnit.DAYS);//保存30天
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sendMessage(MsmVo msmVo) {
        //使用阿里云发送短信
        System.out.println("发送短信成功");
    }
}
