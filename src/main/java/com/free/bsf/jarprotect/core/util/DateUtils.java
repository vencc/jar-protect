package com.free.bsf.jarprotect.core.util;

import com.free.bsf.jarprotect.core.base.BsfException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date defaultFormat(String str) {
        for (String format :  "yyyy-MM-dd HH:mm:ss,yyyy-MM-dd'T'HH:mm:ss,yyyy-MM-dd,yyyyMMddHHmmss".split(",")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(str);
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static String printDistanceTime(long from, long to) {
        long day = 0;//天数差
        long hour = 0;//小时数差
        long min = 0;//分钟数差
        long second=0;//秒数差
        long diff=0 ;//毫秒差
        try {
            long time1 = from;
            long time2 = to;
            diff = time2 - time1;
            day = (long)( (double)diff / (24 * 60 * 60 * 1000));
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            second = diff/1000;
            return ""+day+"天"+hour+"时"+min+"分"+second%60+"秒";
        } catch (Exception e) {
            throw new BsfException(e);
        }
    }
}
