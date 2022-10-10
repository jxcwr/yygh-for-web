package com.atguigu.yygh.orders.service.impl;

import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.orders.mapper.PaymentMapper;
import com.atguigu.yygh.orders.service.OrderInfoService;
import com.atguigu.yygh.orders.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月10日 16:25
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderInfoService orderInfoService;


    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) return;
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + "|" + orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo);
    }

    //实现方法
    //更新订单状态和更新支付记录状态
    @Override
    public void paySuccess(String out_trade_no, Integer weixtype, Map<String, String> resultMap) {
        //1 更新订单状态
        QueryWrapper<OrderInfo> wrapperOrder = new QueryWrapper<>();
        wrapperOrder.eq("out_trade_no", out_trade_no);
        OrderInfo orderInfo = orderInfoService.getOne(wrapperOrder);
        //状态已经支付
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);

        //2 更新支付记录状态
        QueryWrapper<PaymentInfo> wrapperPayment = new QueryWrapper<>();
        wrapperPayment.eq("out_trade_no", out_trade_no);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapperPayment);
        //设置状态
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);
    }


}
