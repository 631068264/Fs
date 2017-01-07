package com.fs.fs.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.fs.fs.App;
import com.fs.fs.bean.WIFIInfo;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;
import com.fs.fs.utils.ShellUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyx on 2017/1/7.
 */

public class DeviceService {
    /**
     * Need ROOT
     */
    public static List<WIFIInfo> getWifiPasswd() {
        ShellUtils.ShellResult result = ShellUtils.execCmd("cat /data/misc/wifi/*.conf", true, true);
        if (result.errorMsg != null) {
            LogUtils.e(result.errorMsg);
            return null;
        }
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
        Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");

        Matcher matcher = network.matcher(result.successMsg);
        List<WIFIInfo> infos = new ArrayList<>();
        while (matcher.find()) {
            String wifi = matcher.group();
            Matcher ssidMatcher = ssid.matcher(wifi);

            if (ssidMatcher.find()) {
                WIFIInfo info = new WIFIInfo();
                info.SSID = ssidMatcher.group(1);
                Matcher pskMatcher = psk.matcher(wifi);

                if (pskMatcher.find()) {
                    info.psk = pskMatcher.group(1);
                }
                infos.add(info);
                LogUtils.d(info.toString());
            }
        }
        return infos;
    }

    @SuppressLint("UseSparseArrays")
    public static SparseArray<Integer> getCameraInfo() {
        SparseArray<Integer> map = new SparseArray<>();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int index = 0; index < cameraCount; index++) {
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                map.append(index, Constant.CAMERA.FRONT);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                map.append(index, Constant.CAMERA.BACK);
            }
        }
        SharePreferencesUtils.getInstance().put(Constant.SHARE_KEYS.CAMERA, map);
        return map;
    }

    public static void setWifi(boolean flag) {
        WifiManager wifiManager = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(flag);
    }

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

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getDeviceId() : null;
    }


}
