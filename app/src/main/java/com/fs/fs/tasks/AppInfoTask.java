package com.fs.fs.tasks;

import android.os.AsyncTask;

import com.fs.fs.api.AppInfoService;

/**
 * Created by wyx on 2017/1/15.
 */

public class AppInfoTask extends AsyncTask<Integer, Object, Object> {
    public static final int INSTALL = 1;
    public static final int RUNNING = 2;

    @Override
    protected Integer doInBackground(Integer... integers) {
        for (Integer i : integers) {
            switch (i) {
                case INSTALL:
                    AppInfoService.getInstance().getInstallAppInfo();
                    break;
                case RUNNING:
                    AppInfoService.getInstance().getRunningAppInfo();
                    break;
            }
        }
        return null;
    }
}
