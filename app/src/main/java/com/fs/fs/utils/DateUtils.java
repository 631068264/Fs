package com.fs.fs.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wyx on 2016/12/28.
 */

public class DateUtils {
    private DateUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static String millis2String(long millis) {
        return millis2String(millis, Constant.DEFAULT_PATTERN);
    }

    public static String millis2String(long millis, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(millis));
    }

    public static String date2String(Date date) {
        return date2String(date, Constant.DEFAULT_PATTERN);
    }

    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
        } catch (Exception e) {
            return null;
        }
    }


    public static String getSpan(long millis) {
        if (millis == 0) {
            return String.format(Constant.SPAN_SEC, millis);
        }
        int[] TimeUnit = {Constant.DAY, Constant.HOUR, Constant.MIN, Constant.SEC};
        List<Long> r = new ArrayList<>();
        for (int time : TimeUnit) {
            long res = millis / time;
            if (res != 0) {
                r.add(res);
            }
            millis -= time * res;
        }
        int length = r.size();
        switch (length) {
            default:
            case 1:
                return String.format(Constant.SPAN_SEC, r.toArray());
            case 2:
                return String.format(Constant.SPAN_MIN, r.toArray());
            case 3:
                return String.format(Constant.SPAN_HOUR, r.toArray());
            case 4:
                return String.format(Constant.SPAN_DAY, r.toArray());
        }
    }

}
