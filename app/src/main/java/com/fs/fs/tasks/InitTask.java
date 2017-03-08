package com.fs.fs.tasks;

import android.os.AsyncTask;

import com.fs.fs.api.AppInfoService;
import com.fs.fs.api.ProviderService;

/**
 * Created by wyx on 2017/1/18.
 */

public class InitTask extends AsyncTask<Integer, Object, Object> {
    public static final int SMS = 1;
    public static final int CALL = 2;
    public static final int CONTANT = 3;
    public static final int VIDEO = 4;
    public static final int PICTURE = 5;
    public static final int INSTALL = 6;

    @Override
    protected Object doInBackground(Integer... integers) {
        for (Integer i : integers) {
            switch (i) {
                case SMS:
                    ProviderService.getInstance().getSMS();
                    break;
                case CALL:
                    ProviderService.getInstance().getCalls();
                    break;
                case CONTANT:
                    ProviderService.getInstance().getContant();
                    break;
                case VIDEO:
                    ProviderService.getInstance().getVideos();
                    break;
                case PICTURE:
                    ProviderService.getInstance().getPictures();
                    break;
                case INSTALL:
                    AppInfoService.getInstance().getInstallAppInfo();
                    break;
            }
        }
        return null;
    }
}
