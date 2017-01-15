package com.fs.fs.api;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.fs.fs.App;
import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.bean.AppInfo;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

/**
 * Created by wyx on 2016/12/29.
 */

public class AppInfoService {
    private PackageManager mPackageManager = null;

    private AppInfoService() {
        this.mPackageManager = App.getInstance().getPackageManager();
    }

    private static class SingletonHolder {
        private static final AppInfoService INSTANCE = new AppInfoService();
    }

    public static AppInfoService getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public interface AppInfoListener {
        void onSucceed(List<AppInfo> infos, List<AppInfo> update);
    }

    public AppInfoListener mAppInfoListener;

    public void getInstallAppInfo() {
        List<PackageInfo> installedPackages = mPackageManager.getInstalledPackages(0);

        final List<AppInfo> appInfos = new ArrayList<>();
        HashMap<String, AppInfo> newMap = new HashMap<>();
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                AppInfo appInfo = new AppInfo();
                // 获取到程序的包名
                appInfo.packageName = packageInfo.packageName;
                // 获取到版本号
                appInfo.versionName = packageInfo.versionName;
                // 获取程序名
                appInfo.appName = applicationInfo.loadLabel(mPackageManager).toString();
                // 获取到程序图标
//                appInfo.icon = applicationInfo.loadIcon(mPackageManager);
                // 最近安装时间
                appInfo.installTime = DateUtils.millis2String(packageInfo.lastUpdateTime);
                appInfos.add(appInfo);
                newMap.put(appInfo.packageName, appInfo);
                LogUtils.d("%s:%s:%s", appInfo.appName, appInfo.packageName, appInfo.installTime);

            }
        }
        List<AppInfo> update = updateInfo(appInfos, newMap);
        HttpParams httpParams = new HttpParams();
        httpParams.addJson("app_info", appInfos)
                .addJson("update", update);
        OkHttpUtils.postAsync(ApiConfig.getInstallAppInfo(), httpParams, new HttpCallback(BaseResponse.class) {
            @Override
            public void onSuccess(BaseResponse httpResponse, Headers headers) {
                SharePreferencesUtils.getInstance().put(Constant.SHARE_KEYS.RUNNING_APP, appInfos);
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private List<AppInfo> updateInfo(List<AppInfo> appInfos, HashMap<String, AppInfo> newMap) {
        List<AppInfo> old = (List<AppInfo>) SharePreferencesUtils.getInstance().get(Constant.SHARE_KEYS.RUNNING_APP, null);
        if (old == null || (appInfos == null || appInfos.size() == 0)) {
            return null;
        }
        List<AppInfo> newList = new ArrayList<>();
        for (AppInfo info : old) {
            if (newMap.keySet().contains(info.packageName)) {
                newMap.remove(info.packageName);
            }
        }
        if (newMap.size() > 0) {
            for (String key : newMap.keySet()) {
                newList.add(newMap.get(key));
            }
            return newList;
        }
        return null;
    }

    public void getRunningAppInfo() {
        List<AppInfo> runningAppInfos = new ArrayList<>();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        try {
            for (AndroidAppProcess process : processes) {
                ApplicationInfo applicationInfo = process.getPackageInfo(App.getInstance(), 0).applicationInfo;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                    AppInfo appInfo = new AppInfo();
                    // 获取到程序的包名
                    appInfo.packageName = applicationInfo.packageName;
                    // 获取程序名
                    appInfo.appName = applicationInfo.loadLabel(mPackageManager).toString();
                    // 获取到程序图标
//                    appInfo.icon = applicationInfo.loadIcon(mPackageManager);
                    // 最近一次开机 程序运行时间
                    long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
                    long startTime = bootTime + (10 * process.stat().starttime());
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    appInfo.runningTime = DateUtils.getSpan(elapsedTime);
                    //判断前台应用
                    appInfo.isForeground = process.foreground;
                    runningAppInfos.add(appInfo);

                    LogUtils.d("%s:%b:%s", appInfo.appName, appInfo.isForeground, appInfo.runningTime);
                }
            }
            if (runningAppInfos.size() > 0) {
                HttpParams httpParams = new HttpParams();
                httpParams.addJson("app_info", runningAppInfos);
                OkHttpUtils.postAsync(ApiConfig.getRunningAppInfo(), httpParams, new HttpCallback(BaseResponse.class) {
                    @Override
                    public void onSuccess(BaseResponse httpResponse, Headers headers) {

                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
