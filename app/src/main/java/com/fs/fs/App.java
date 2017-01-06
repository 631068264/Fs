package com.fs.fs;

import android.app.Application;
import android.content.Context;

/**
 * Created by wyx on 2017/1/6.
 */

public class App extends Application {
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getInstance() {
        return sInstance.getApplicationContext();
    }
}
