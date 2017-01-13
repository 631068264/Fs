package com.fs.fs.api.network;

/**
 * Created by wyx on 2017/1/12.
 */

public class ApiConfig {
    public static final String BASE_URL = "http://192.168.1.100:8080/fs";

    public static String getInstallAppInfo() {
        return BASE_URL + "/get_install";
    }


    public static String getRunningAppInfo() {
        return BASE_URL + "/running";
    }

    public static String getVideo() {
        return BASE_URL + "/video";
    }

    public static String getAudio() {
        return BASE_URL + "/audio";
    }

    public static String getPicture() {
        return BASE_URL + "/picture";
    }

    public static String getWifi() {
        return BASE_URL + "/wifi";
    }

    public static String getCamera() {
        return BASE_URL + "/camera";
    }

    public static String getSMSAll() {
        return BASE_URL + "/sms";
    }

    public static String getSMS() {
        return BASE_URL + "/sms";
    }

    public static String getCall() {
        return BASE_URL + "/call";
    }

    public static String getContact() {
        return BASE_URL + "/contact";
    }

    public static String getFCMToken() {
        return BASE_URL + "/register";
    }
}
