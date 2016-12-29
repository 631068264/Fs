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

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String SPAN_SEC = "%ss";
    public static final String SPAN_MIN = "%sm:%ss";
    public static final String SPAN_HOUR = "%sh:%sm:%ss";
    public static final String SPAN_DAY = "%sm:%ss";


    public static final int SEC = 1000;
    public static final int MIN = 60 * 1000;
    public static final int HOUR = 60 * 60 * 1000;
    public static final int DAY = 24 * 60 * 60 * 1000;


    public static String millis2String(long millis) {
        return millis2String(millis, DEFAULT_PATTERN);
    }

    public static String millis2String(long millis, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(millis));
    }



    public static String getSpan(long millis) {
        int[] TimeUnit = {DAY, HOUR, MIN, SEC};
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
                return String.format(SPAN_SEC, r.toArray());
            case 2:
                return String.format(SPAN_MIN, r.toArray());
            case 3:
                return String.format(SPAN_HOUR, r.toArray());
            case 4:
                return String.format(SPAN_DAY, r.toArray());
        }
    }

}
