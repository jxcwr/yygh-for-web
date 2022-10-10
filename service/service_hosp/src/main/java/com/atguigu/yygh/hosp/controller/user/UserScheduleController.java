package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description 用户医院部门排班信息
 * @date 2022年10月05日 12:00
 */
@Api(tags = "用户医院部门排班信息")
@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("/auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getBookingSchedule(@PathVariable Integer page,
                                @PathVariable Integer limit,
                                @PathVariable String hoscode,
                                @PathVariable String depcode) {

        Map<String, Object> map = scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    /**
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return com.atguigu.yygh.common.result.R
     * @description: 查询当天排班的信息
     * @author 靳雪超
     * @date: 2022/10/5 20:49
     */
    @ApiOperation("查询当天排班的信息")
    @GetMapping("/auth/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate) {

        List<Schedule> details = scheduleService.findDetailByWorkDateAndHoscodeAndDepcode(hoscode, depcode, workDate);
        return R.ok().data("scheduleList", details);
    }

    /**
     * @param id
     * @return com.atguigu.yygh.common.result.R
     * @description: 根据id获取排班详情
     * @author 靳雪超
     * @date: 2022/10/5 20:03
     */
    @ApiOperation(value = "根据id获取排班详情")
    @GetMapping("/getSchedule/{id}")
    public R getScheduleInfo(@PathVariable String id) {

        Schedule schedule = scheduleService.getScheduleInfo(id);
        return R.ok().data("schedule", schedule);
    }

    /**
     * @param scheduleID
     * @return com.atguigu.yygh.vo.hosp.ScheduleOrderVo
     * @description: 远程调用，获取排班医生的信息
     * @author 靳雪超
     * @date: 2022/10/5 21:22
     */
    @ApiOperation("远程调用，获取排班医生的信息")
    @GetMapping("/inner/getScheduleById/{scheduleID}")
    public ScheduleOrderVo getScheduleById(@PathVariable("scheduleID") String scheduleID) {

        return scheduleService.getScheduleById(scheduleID);
    }
}
