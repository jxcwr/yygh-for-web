package com.atguigu.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月19日 21:26
 */
public class HttpRequestHelper {

    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {

        Map<String, Object> resultMap=new HashMap<>();

        Set<Map.Entry<String, String[]>> entrySet = paramMap.entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {

            resultMap.put(entry.getKey(), entry.getValue()[0]);
        }

        return resultMap;
    }
}
