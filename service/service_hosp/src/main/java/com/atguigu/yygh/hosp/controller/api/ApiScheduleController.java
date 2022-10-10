package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月20日 20:47
 */

@Api("科室排班列表")
@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * @description:  保存第三方医院上传的医院科室排班信息
     * @author 靳雪超
     * @date: 2022/9/20 20:53
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("保存第三方医院上传的医院科室排班信息")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest httpServletRequest){
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());
        String signKey = (String) resultMap.get("sign");

        if (signKey == null || !signKey.equals(MD5.encrypt(scheduleService.getHoscode((String) resultMap.get("hoscode"))))){
            return Result.fail();
        }
        scheduleService.save(resultMap);
        return Result.ok();
    }

    /**
     * @description:  分页查询值班信息
     * @author 靳雪超
     * @date: 2022/9/20 22:19
     * @param httpServletRequest
     * @return com.atguigu.yygh.hosp.result.Result<com.atguigu.yygh.model.hosp.Schedule>
     */
    ///api/hosp/schedule/list
    @ApiOperation("分页查询值班信息")
    @PostMapping("/schedule/list")
    public Result<Page> pageSchedule(HttpServletRequest httpServletRequest){

        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());
        String sign = (String) resultMap.get("sign");
        String hoscode = (String) resultMap.get("hoscode");

        //检验
        if (sign == null || !sign.equals(MD5.encrypt(scheduleService.getHoscode(hoscode)))){
            Result.fail();
        }

        String pageNum = (String) resultMap.get("page");
        String pageSize = (String) resultMap.get("limit");
        String depcode = (String) resultMap.get("depcode");

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> page = scheduleService.pageSchedule(Integer.parseInt(pageNum),Integer.parseInt(pageSize),scheduleQueryVo);

        return Result.ok(page);
    }

    ///api/hosp/schedule/remove
    /**
     * @description:  删除值班信息
     * @author 靳雪超
     * @date: 2022/9/21 9:03
     * @return com.atguigu.yygh.hosp.result.Result
     */
    @ApiOperation("删除值班信息")
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest httpServletRequest){

        Map<String, Object> resultMap = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());

        String sign = (String) resultMap.get("sign");
        String hoscode = (String) resultMap.get("hoscode");

        //检验
        if (sign == null || !sign.equals(MD5.encrypt(scheduleService.getHoscode(hoscode)))){
            Result.fail();
        }
        String hosScheduleId = (String) resultMap.get("hosScheduleId");
        if (scheduleService.deleteSchedule(hoscode,hosScheduleId)){
            return Result.ok();
        }else
            return Result.fail();
    }
}
