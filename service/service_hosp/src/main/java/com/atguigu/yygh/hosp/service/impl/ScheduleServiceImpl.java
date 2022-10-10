package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.WeekUtil;
import com.atguigu.yygh.model.hosp.*;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月20日 20:55
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(Map<String, Object> resultMap) {

        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Schedule.class);

        Schedule scheduleById = scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (scheduleById == null) {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);

        } else {
            schedule.setCreateTime(scheduleById.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(scheduleById.getIsDeleted());
            schedule.setStatus(scheduleById.getStatus());
            scheduleRepository.save(schedule);
        }


    }

    @Override
    public String getHoscode(String hoscode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);
        if (hospitalSet != null) {
            return hospitalSet.getSignKey();
        } else {
            throw new YyghException(20001, "此医院不存在...");
        }
    }

    @Override
    public Page<Schedule> pageSchedule(Integer parseNum, Integer pageSize, ScheduleQueryVo scheduleQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(parseNum - 1, pageSize, sort);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> pages = scheduleRepository.findAll(example, pageable);
        return pages;
    }

    @Override
    public boolean deleteSchedule(String hoscode, String hosScheduleId) {

        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (schedule == null) {
            return false;
        }
        scheduleRepository.deleteById(schedule.getId());
        return true;
    }

    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {

        //1 根据医院编号 和 科室编号 查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //2 根据工作日workDate期进行分组,创建聚合
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        //3 统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                //分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        //调用方法，最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询的总记录数
        Aggregation aggregationTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
        );

        //调用方法，执行
        AggregationResults<BookingScheduleRuleVo> aggregateResultTotal
                = mongoTemplate.aggregate(aggregationTotal, Schedule.class, BookingScheduleRuleVo.class);

        //拿到总记录数
        Integer total = aggregateResultTotal.getMappedResults().size();

        //把时间转换为week
        for (BookingScheduleRuleVo ruleVo : bookingScheduleRuleVoList) {
            Date workDate = ruleVo.getWorkDate();
            ruleVo.setDayOfWeek(WeekUtil.getDayOfWeek(new DateTime(workDate)));
        }

        //包装最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        //将分页好的数据装进去
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        //将总记录数放进去
        result.put("total", total);

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalInfo(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospital.getHosname());
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, Date date) {
        List<Schedule> detailScheduleList = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, date);

        //拿到医院编号，和科室编号，工作时间进行转换
        for (Schedule schedule : detailScheduleList) {
            this.packageSchedule(schedule);
        }

        return detailScheduleList;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {

        //校验医院是否存在
        Hospital hospitalInfo = hospitalService.getHospitalInfo(hoscode);
        if (hospitalInfo == null) {
            throw new YyghException(20001, "医院不存在...");
        }

        //查询此医院的规则
        BookingRule bookingRule = hospitalInfo.getBookingRule();

        IPage<Date> dateIPage = this.getListDate(page, limit, bookingRule);
        List<Date> records = dateIPage.getRecords();

        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(records);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> results = aggregate.getMappedResults();

        //转换成map
        Map<Date, BookingScheduleRuleVo> collect = results.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        //封装好的聚合对象数据
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if (bookingScheduleRuleVo == null) {     //当天没有医生值班
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
//                bookingScheduleRuleVo.setStatus(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);//当天医生剩余的总预约数
                bookingScheduleRuleVo.setReservedNumber(0);
                bookingScheduleRuleVo.setDocCount(0);
            }

            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(WeekUtil.getDayOfWeek(new DateTime(date)));
            bookingScheduleRuleVo.setStatus(0);

            //如果是第一页的第一条数据的话判断是否过了挂号时间
            if (i == 0 && page == 1 && this.getDateTime(new Date(), bookingRule.getStopTime()).isBeforeNow()) {
                bookingScheduleRuleVo.setStatus(-1);//表示停止挂号
            }

            //如果是最后一页最后一条
            if (page == dateIPage.getPages() && i == records.size() - 1) {
                bookingScheduleRuleVo.setStatus(1);
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", dateIPage.getTotal());
        result.put("bookingScheduleList", bookingScheduleRuleVoList);

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    @Override
    public List<Schedule> findDetailByWorkDateAndHoscodeAndDepcode(String hoscode, String depcode, String workDate) {
        return scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
    }

    @Override
    public Schedule getScheduleInfo(String id) {

        Schedule schedule = scheduleRepository.findById(id).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleById(String scheduleID) {
        Schedule schedule = scheduleRepository.findById(scheduleID).get();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        BeanUtils.copyProperties(schedule, scheduleOrderVo);

        Hospital hospitalInfo = hospitalService.getHospitalInfo(schedule.getHoscode());
        //设置医院名字
        scheduleOrderVo.setHosname(hospitalInfo.getHosname());
        //设置部门的名字
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));

        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //退号截止时间
        Date dateTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(hospitalInfo.getBookingRule().getQuitDay()).toDate(), hospitalInfo.getBookingRule().getQuitTime()).toDate();
        scheduleOrderVo.setQuitTime(dateTime);

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), hospitalInfo.getBookingRule().getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(hospitalInfo.getBookingRule().getCycle()).toDate(), hospitalInfo.getBookingRule().getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(schedule.getWorkDate(), hospitalInfo.getBookingRule().getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    @Override
    public Boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {
/*        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        schedule.setAvailableNumber(availableNumber);*/

        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setId(scheduleId);
        schedule.setAvailableNumber(availableNumber);
        scheduleRepository.save(schedule);
        return true;
    }

    /**
     * @param page
     * @param limit
     * @param bookingRule
     * @return com.baomidou.mybatisplus.core.metadata.IPage<java.util.Date>
     * @description: 获取可预约日期分页数据
     * @author 靳雪超
     * @date: 2022/10/5 16:21
     */
    private IPage<Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {

        //拿到预约周期
        Integer cycle = bookingRule.getCycle();
        //当天放号时间
//        String releaseTime = bookingRule.getReleaseTime();
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //如果当天放号时间已过，则预约周期后一天为即将放号时间，周期加1
        if (releaseTime.isBeforeNow()) {    //当前时间在预约时间之后，所以预约周期加一天
            cycle = cycle + 1;
        }

        //可预约所有日期，最后一天显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            //计算当前预约日期
//            DateTime curDateTime = new DateTime().plusDays(i);
//            String dateString = curDateTime.toString("yyyy-MM-dd");
//            dateList.add(new DateTime(dateString).toDate());
            dateList.add(new DateTime(new DateTime().plusDays(i).toString("yyyy-MM-dd")).toDate());
        }

        //日期分页，由于预约周期不一样，页面一排最多显示7天数据，多了就要分页显示
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        if (end > dateList.size()) end = dateList.size();
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }

        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }


    //工具函数
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", WeekUtil.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }
}
