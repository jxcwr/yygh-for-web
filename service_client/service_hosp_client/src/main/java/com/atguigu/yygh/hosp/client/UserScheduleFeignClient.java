package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp") //被调用方在注册中心的名称
public interface UserScheduleFeignClient {

    /**
     * @param scheduleID
     * @return com.atguigu.yygh.vo.hosp.ScheduleOrderVo
     * @description: 远程调用，获取排班医生的信息
     * @author 靳雪超
     * @date: 2022/10/5 21:23
     */
    @GetMapping("/user/hosp/schedule/inner/getScheduleById/{scheduleID}")
    ScheduleOrderVo getScheduleById(@PathVariable String scheduleID);
}
