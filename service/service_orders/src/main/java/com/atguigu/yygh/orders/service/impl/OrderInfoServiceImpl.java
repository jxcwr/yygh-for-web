package com.atguigu.yygh.orders.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.hosp.client.UserScheduleFeignClient;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.mq.constantClass.MqConst;
import com.atguigu.yygh.mq.service.RabbitService;
import com.atguigu.yygh.orders.mapper.OrderInfoMapper;
import com.atguigu.yygh.orders.service.OrderInfoService;
import com.atguigu.yygh.orders.utils.HttpRequestHelper;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author jxc
 * @since 2022-10-05
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private UserScheduleFeignClient userScheduleFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public Long saveOrder(String scheduleId, Long patientId) {

        //1.先获取医生的排班信息
        ScheduleOrderVo scheduleById = userScheduleFeignClient.getScheduleById(scheduleId);
        /*
         *@description:判断是否超过时间
         */
        if (new DateTime(scheduleById.getStopTime()).isBeforeNow()) {
            throw new YyghException(20001, "超过了挂号截止时间...");
        }

        //2.获取就诊人的信息
        Patient patientOrder = patientFeignClient.getPatientOrder(patientId);
        //3.从平台请求第三方医院，看看是否能挂号
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleById.getHoscode());
        paramMap.put("depcode", scheduleById.getDepcode());
        paramMap.put("hosScheduleId", scheduleById.getHosScheduleId());
        paramMap.put("reserveDate", scheduleById.getReserveDate());
        paramMap.put("reserveTime", scheduleById.getReserveTime());
        paramMap.put("amount", scheduleById.getAmount());//挂号费用
        paramMap.put("name", patientOrder.getName());
        paramMap.put("certificatesType", patientOrder.getCertificatesType());
        paramMap.put("certificatesNo", patientOrder.getCertificatesNo());
        paramMap.put("sex", patientOrder.getSex());
        paramMap.put("birthdate", patientOrder.getBirthdate());
        paramMap.put("phone", patientOrder.getPhone());
        paramMap.put("isMarry", patientOrder.getIsMarry());
        paramMap.put("provinceCode", patientOrder.getProvinceCode());
        paramMap.put("cityCode", patientOrder.getCityCode());
        paramMap.put("districtCode", patientOrder.getDistrictCode());
        paramMap.put("address", patientOrder.getAddress());
        //联系人
        paramMap.put("contactsName", patientOrder.getContactsName());
        paramMap.put("contactsCertificatesType", patientOrder.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patientOrder.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patientOrder.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if (jsonObject != null && jsonObject.getInteger("code") == 200) {

            // 3.2 如果返回成功，得到返回其他数据
            JSONObject jsonObjectResult = jsonObject.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObjectResult.getString("hosRecordId");
            //预约序号
            Integer number = jsonObjectResult.getInteger("number");

            //取号时间
            String fetchTime = jsonObjectResult.getString("fetchTime");

            //取号地址
            String fetchAddress = jsonObjectResult.getString("fetchAddress");


            //4 如果医院接口返回成功，添加上面三部分数据到数据库
            OrderInfo orderInfo = new OrderInfo();
            //设置添加数据--排班数据
            BeanUtils.copyProperties(scheduleById, orderInfo);
            //设置添加数据--就诊人数据
            //订单号
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleById.getHosScheduleId());
            orderInfo.setUserId(patientOrder.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patientOrder.getName());
            orderInfo.setPatientPhone(patientOrder.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());//未支付

            //设置添加数据--医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);

            //调用方法添加
            baseMapper.insert(orderInfo);

            //TODO 5 根据医院返回数据，更新排班数量
            //排班可预约数
            Integer reservedNumber = jsonObjectResult.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObjectResult.getInteger("availableNumber");
            //更新平台上医生的可预约数
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patientOrder.getPhone());
            msmVo.setTemplateCode("预约成功！您已经预约了${reserveDate}点的${name}医生的好，请不要迟到");
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            orderMqVo.setMsmVo(msmVo);
            //发送消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);


            //TODO 6 给就诊人发送短信

            //7 返回订单号
            return orderInfo.getId();
        } else {
            throw new YyghException(20001, "号源已满");
        }
    }

    @Override
    public IPage<OrderInfo> selectPageByConditions(Long userId, Integer pageNum, Integer limit, OrderQueryVo orderQueryVo) {

        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //对条件值进行非空判断

        Page page = new Page(pageNum, limit);
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper();

        //TODO: 判断当前用户id非空，使用用户id查询  !!!

        if (!StringUtils.isEmpty(userId)) {
            queryWrapper.eq("user_id", userId);
        }

        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("hosname", name);
        }
        if (!StringUtils.isEmpty(patientId)) {
            queryWrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            queryWrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            queryWrapper.ge("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd);
        }

        Page<OrderInfo> selectPage = baseMapper.selectPage(page, queryWrapper);

        //编号变成对应值封装
        selectPage.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return selectPage;
    }

    @Override
    public OrderInfo getOrderDetail(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }


    /**
     * @param orderInfo
     * @return com.atguigu.yygh.model.order.OrderInfo
     * @description: 工具类，编号变成对应值封装
     * @author 靳雪超
     * @date: 2022/10/7 14:37
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
