package com.atguigu.yygh.orders.service.impl;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.orders.prop.WeiXinPayProperties;
import com.atguigu.yygh.orders.service.OrderInfoService;
import com.atguigu.yygh.orders.service.PaymentService;
import com.atguigu.yygh.orders.service.WeixinService;
import com.atguigu.yygh.orders.utils.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月10日 16:47
 */
@Service
public class WeixinServicePayImpl implements WeixinService {

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private WeiXinPayProperties weiXinPayProperties;


    @Override
    public String createNative(Long orderId) {
        //1.根据id，查找订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);

        //2.保存支付记录信息
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        //3.请求微信服务器，获取微信支付的url地址
        //1、设置参数
        Map paramMap = new HashMap();
        paramMap.put("appid", weiXinPayProperties.getAppid());
        paramMap.put("mch_id", weiXinPayProperties.getPartner());//商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊" + orderInfo.getDepname();
        paramMap.put("body", body);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());//商品订单号
        //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee", "1");//为了测试，一分钱
        paramMap.put("spbill_create_ip", "127.0.0.1");//假数据
        /*
            使用的是第二种方式，主动去调用微信服务器，查看订单信息，订单状态
         */
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");//假数据
        paramMap.put("trade_type", "NATIVE");
        //2、HTTPClient来根据URL访问第三方接口并且传递参数
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        String signedXml = null;
        String url = "";
        try {
            signedXml = WXPayUtil.generateSignedXml(paramMap, weiXinPayProperties.getPartnerkey());
            client.setXmlParam(signedXml);
            client.setHttps(true);
            //发送请求
            client.post();
            //接收到返回的参数
            String content = client.getContent();
            url = WXPayUtil.xmlToMap(content).get("code_url");
        } catch (Exception e) {
            throw new YyghException(20001, "微信支付失败");
        }

        return url;
    }


    //实现方法
    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        try {
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            //1、封装参数
            Map paramMap = new HashMap<>();
            paramMap.put("appid", weiXinPayProperties.getAppid());
            paramMap.put("mch_id", weiXinPayProperties.getPartner());
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, weiXinPayProperties.getPartnerkey()));
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据，转成Map
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、返回
            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }
}
