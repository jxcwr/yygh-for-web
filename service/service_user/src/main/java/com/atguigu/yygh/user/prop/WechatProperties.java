package com.atguigu.yygh.user.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月27日 21:26
 */
@ConfigurationProperties(prefix = "weixin")
@Data
@Component

//1.@EnableConfigurationProperties(value = WechatProperties.class)+@ConfigurationProperties(prefix = "weixin")
//2.@Component+@Value
//3.@Component+@ConfigurationProperties(prefix = "weixin")
public class WechatProperties {
    private String appid;
    private String appsecret;
    private String redirecturl;
}
