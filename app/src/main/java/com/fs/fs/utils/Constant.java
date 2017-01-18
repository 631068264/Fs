package com.fs.fs.utils;

/**
 * Created by wyx on 2016/12/30.
 */

public class Constant {
    /**
     * DateUtils
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String SPAN_SEC = "%ss";
    public static final String SPAN_MIN = "%sm:%ss";
    public static final String SPAN_HOUR = "%sh:%sm:%ss";
    public static final String SPAN_DAY = "%sm:%ss";
    public static final int SEC = 1000;

    public static final int MIN = 60 * 1000;
    public static final int HOUR = 60 * 60 * 1000;
    public static final int DAY = 24 * 60 * 60 * 1000;
    /**
     * LogUtils
     */
    public static final int DEBUG = 0;
    public static final int ERROR = 2;
    public static final int INFO = 4;

    /**
     * SharePreferenceUtils
     */
    public static final String SHARE_FILE_NAME = "SHARE_DATA";

    public static final class SHARE_KEYS {
        public static final String SMS = "sms";
        public static final String CAMERA = "camera";
        public static final String RUNNING_APP = "running_app";
        public static final String CALL = "call";
        public static final String CONTANT = "contant";
        public static final String PICTURE = "picture";
        public static final String VIDEO = "video";
        public static final String FCM = "fcm";
    }

    /**
     * DeviceUtils
     */
    public static final class CAMERA {
        public static final int FRONT = 1;
        public static final int BACK = 0;
        public static final int NONE = -1;
    }

    /**
     * Command
     */
    public enum Command {
        app,
        run_app,
        photo,
        audio,
        stop_audio,
        video,
        stop_video,
        locate_on,
        locate_off,
        track,
    }
}
