package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月05日 21:23
 */
@FeignClient("service-user") //被调用方在注册中心的名称
public interface PatientFeignClient {

    @GetMapping("/user/patient/inner/get/{id}")
    Patient getPatientOrder(@PathVariable("id") Long id);
}
