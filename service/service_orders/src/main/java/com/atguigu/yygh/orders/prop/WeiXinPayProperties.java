package com.atguigu.yygh.orders.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月10日 16:21
 */
@Component
@ConfigurationProperties(prefix = "weipay")
@PropertySource(value = "classpath:weipay.properties")
@Data
public class WeiXinPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
}
