package com.atguigu.yygh.orders.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.orders.service.PaymentService;
import com.atguigu.yygh.orders.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年10月10日 16:23
 */
@Api(tags = "用户微信支付接口")
@RequestMapping("/api/order/weixin")
@RestController
public class WeiXinPayController {
    @Autowired
    private WeixinService weixinPayService;
    @Autowired
    private PaymentService paymentService;

    /**
     * 下单 生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public R createNative(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        String url = weixinPayService.createNative(orderId);
        return R.ok().data("url", url);
    }

    /**
     * @return com.atguigu.yygh.common.result.R
     * @description: 主动查询微信服务器，查看订单状态
     * @author 靳雪超
     * @date: 2022/10/10 18:21
     */
    @ApiOperation("主动查询微信服务器，查看订单状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public R getPayStatus(@PathVariable Long orderId) {
        Map<String, String> map = weixinPayService.queryPayStatus(orderId);

        if (map == null) {
            return R.ok().message("支付失败");
        }
        if ("SUCCESS".equals(map.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = map.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), map);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}
