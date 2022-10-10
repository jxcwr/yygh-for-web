package com.atguigu.yygh.hosp.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * @author jxc
 * @version 1.0
 * @description TODD
 * @date 2022年09月26日 14:23
 */
public class WeekUtil {

    public static String getDayOfWeek(DateTime dateTime) {

        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
