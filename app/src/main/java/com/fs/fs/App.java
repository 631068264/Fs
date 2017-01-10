package com.fs.fs;

import android.app.Application;
import android.content.Context;

import com.fs.fs.api.network.core.OkHttpConfig;

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
    }


    public static Context getInstance() {
        return sInstance.getApplicationContext();
    }
}
