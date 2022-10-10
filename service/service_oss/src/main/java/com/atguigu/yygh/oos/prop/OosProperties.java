package com.atguigu.yygh.oos.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月29日 21:57
 */
@ConfigurationProperties(prefix = "aliyun.oss.file")
@PropertySource(value = {"classpath:oss.properties"})
@Data
@Component
public class OosProperties {

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;
}
