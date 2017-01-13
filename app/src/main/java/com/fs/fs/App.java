package com.fs.fs;

import android.app.Application;
import android.content.Context;

import com.fs.fs.api.network.core.OkHttpConfig;
import com.fs.fs.utils.DeviceUtils;

/**
 * Created by wyx on 2017/1/6.
 */

public class App extends Application {
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        OkHttpConfig.init(null, null);
        OkHttpConfig.getInstance()
                .updateCommonHeaders("X-IMEI", DeviceUtils.getIMEI())
                .updateCommonHeaders("X-Brand", DeviceUtils.brand)
                .updateCommonHeaders("X-Model", DeviceUtils.model)
                .updateCommonHeaders("X-Product", DeviceUtils.product)
                .updateCommonHeaders("X_Lang", DeviceUtils.lang)
                .updateCommonHeaders("X_SystemVersion", String.valueOf(DeviceUtils.osVersion));
    }


    public static Context getInstance() {
        return sInstance.getApplicationContext();
    }
}
