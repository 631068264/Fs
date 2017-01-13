package com.fs.fs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.fs.fs.App;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by wyx on 2017/1/12.
 */

public class DeviceUtils {
    // hardware
    public static final String brand = Build.BRAND;
    public static final String model = Build.MODEL;
    public static final String product = Build.PRODUCT;
    public static final int osVersion = Build.VERSION.SDK_INT;
    public static final String lang = Locale.getDefault().toString();

    /**
     * 判断设备是否root
     */
    public static boolean isRoot() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/",
                "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("HardwareIds")
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? hexlify(md5(tm.getDeviceId().getBytes())) : null;
    }

    public static void setWifi(boolean flag) {
        WifiManager wifiManager = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(flag);
    }


    private static byte[] md5(byte[] src) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null; // This should never happens
        }
        md5.update(src);
        return md5.digest();
    }

    private static String hexlify(byte[] src) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            String hex = Integer.toHexString(0xFF & src[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & src[i]));
        }
        return hexString.toString();
    }

    @Nullable
    private static byte[] unhexlify(String s) {
        if ((s.length() & 1) != 0) {
            return null;
        }
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) +
                    Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
