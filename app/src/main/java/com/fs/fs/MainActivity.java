package com.fs.fs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.fs.fs.bean.AppInfo;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.Logger;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRunningAppInfo(MainActivity.this);
        Logger.d("__________________end_____________");
        getInstallAppInfo();
    }


    private List<AppInfo> getInstallAppInfo() {
        List<AppInfo> appInfos = new ArrayList<>();
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //用户程序
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                AppInfo appInfo = new AppInfo();
                // 获取到程序的包名
                appInfo.packageName = packageInfo.packageName;
                // 获取到版本号
                appInfo.versionName = packageInfo.versionName;
                // 获取程序名
                appInfo.appName = applicationInfo.loadLabel(getPackageManager()).toString();
                // 获取到程序图标
                appInfo.icon = applicationInfo.loadIcon(getPackageManager());
                appInfo.versionCode = packageInfo.versionCode;
                // 最近安装时间
                appInfo.installTime = DateUtils.millis2String(packageInfo.lastUpdateTime);
                appInfos.add(appInfo);

                Logger.d("%s:%s:%s", appInfo.appName, appInfo.packageName, appInfo.installTime);
            }
        }
        return appInfos;
    }

    private List<AppInfo> getRunningAppInfo(Context context) {
        List<AppInfo> runningAppInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        try {
            for (AndroidAppProcess process : processes) {

                ApplicationInfo applicationInfo = process.getPackageInfo(context, 0).applicationInfo;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                    AppInfo appInfo = new AppInfo();
                    // 获取到程序的包名
                    appInfo.packageName = applicationInfo.packageName;
                    // 获取程序名
                    appInfo.appName = applicationInfo.loadLabel(pm).toString();
                    // 获取到程序图标
                    appInfo.icon = applicationInfo.loadIcon(pm);
                    // 最近一次开机 程序运行时间
                    long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
                    long startTime = bootTime + (10 * process.stat().starttime());
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    appInfo.runningTime = DateUtils.getSpan(elapsedTime);
                    //判断前台应用
                    appInfo.isForeground = process.foreground;
                    runningAppInfos.add(appInfo);

                    Logger.d("%s:%b:%s", appInfo.appName, appInfo.isForeground, appInfo.runningTime);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return runningAppInfos;
        }
    }

}
