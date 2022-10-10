package com.atguigu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn") //被调用方在注册中心的名称
public interface DictFeignClient {


    /**
     * 获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/dict/getName/{parentDictCode}/{value}")
    String getName(@PathVariable("parentDictCode") String parentDictCode, @PathVariable("value") Long value);

    /**
     * 获取数据字典名称
     * @param value
     * @return
     */
    @GetMapping(value = "/admin/cmn/dict/getValue/{value}")
    String getValue(@PathVariable("value") Long value);

}
