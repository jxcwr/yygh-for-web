package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> resultMap);

    String getHoscode(String hoscode);

    Page<Schedule> pageSchedule(Integer parseNum, Integer pageSize, ScheduleQueryVo scheduleQueryVo);

    boolean deleteSchedule(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, Date date);


    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    List<Schedule> findDetailByWorkDateAndHoscodeAndDepcode(String hoscode, String depcode, String workDate);

    Schedule getScheduleInfo(String id);

    ScheduleOrderVo getScheduleById(String scheduleID);

    Boolean updateAvailableNumber(String scheduleId, Integer availableNumber);
}

