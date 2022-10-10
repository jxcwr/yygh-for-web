package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date date);
}
