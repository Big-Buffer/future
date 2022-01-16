package com.future.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/15 18:56
 */
public class DateUtils {

    public static Date getFormatDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
    }

    public static String getDateDiff(String endDate) throws ParseException {
        Date nowDate = new Date();
        Date end = getFormatDate(endDate);
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = end.getTime() - nowDate.getTime();
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
