package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月26日 11:50
 */
@Api(tags = "医院排班信息")
@RestController
@RequestMapping("/admin/hosp/schedule")
public class AdminScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return com.atguigu.yygh.common.result.R
     * @description: 根据医院编号 和 科室编号 ，查询排班规则数据
     * @author 靳雪超
     * @date: 2022/9/26 15:31
     */
    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getScheduleRule(@PathVariable long page,
                             @PathVariable long limit,
                             @PathVariable String hoscode,
                             @PathVariable String depcode) {
        Map<String, Object> map
                = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    /**
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return com.atguigu.yygh.common.result.R
     * @description: //根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @author 靳雪超
     * @date: 2022/9/26 16:17
     */
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate) {
        Date date = new DateTime(workDate).toDate();
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, date);
        return R.ok().data("list", list);
    }
}
