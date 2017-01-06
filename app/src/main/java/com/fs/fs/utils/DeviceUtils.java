package com.fs.fs.utils;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.util.SparseArray;

import com.fs.fs.bean.WIFIInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyx on 2017/1/6.
 */

public class DeviceUtils {

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
                map.append(Constant.CAMERA.FRONT, index);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                map.append(Constant.CAMERA.BACK, index);
            }
        }
        return map;
    }
}
