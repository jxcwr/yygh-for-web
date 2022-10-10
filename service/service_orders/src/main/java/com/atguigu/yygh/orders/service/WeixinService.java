package com.atguigu.yygh.orders.service;

import java.util.Map;

public interface WeixinService {

    String createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);
}
