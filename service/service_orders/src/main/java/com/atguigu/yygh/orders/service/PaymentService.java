package com.atguigu.yygh.orders.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    /**
     * 保存交易记录
     *
     * @param orderInfo
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);

    //更新支付状态
//outTradeNo  交易号
//paymentType  支付类型 微信 支付宝
//paramMap 调用微信查询支付状态接口返回map集合
    void paySuccess(String out_trade_no, Integer weixtype, Map<String, String> resultMap);
}