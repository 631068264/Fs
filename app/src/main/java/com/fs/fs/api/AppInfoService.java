package com.fs.fs.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.fs.fs.bean.AppInfo;
import com.fs.fs.bean.WIFIInfo;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.ShellUtils;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyx on 2016/12/29.
 */

public class AppInfoService {
    private Context mContext = null;
    private PackageManager mPackageManager = null;

    public AppInfoService(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
    }

    public interface AppInfoListener {
        void onSucceed(List<AppInfo> appInfos);
    }

    public AppInfoListener mAppInfoListener;

    public void getInstallAppInfo(AppInfoListener listener) {
        mAppInfoListener = listener;
        List<AppInfo> appInfos = new ArrayList<>();
        List<PackageInfo> installedPackages = mPackageManager.getInstalledPackages(0);
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
                appInfo.icon = applicationInfo.loadIcon(mPackageManager);
                // 最近安装时间
                appInfo.installTime = DateUtils.millis2String(packageInfo.lastUpdateTime);
                appInfos.add(appInfo);
                LogUtils.d("%s:%s:%s", appInfo.appName, appInfo.packageName, appInfo.installTime);

            }
        }
        mAppInfoListener.onSucceed(appInfos);
    }

    public void getRunningAppInfo(AppInfoListener listener) {
        mAppInfoListener = listener;
        List<AppInfo> runningAppInfos = new ArrayList<>();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        try {
            for (AndroidAppProcess process : processes) {
                ApplicationInfo applicationInfo = process.getPackageInfo(mContext, 0).applicationInfo;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                    AppInfo appInfo = new AppInfo();
                    // 获取到程序的包名
                    appInfo.packageName = applicationInfo.packageName;
                    // 获取程序名
                    appInfo.appName = applicationInfo.loadLabel(mPackageManager).toString();
                    // 获取到程序图标
                    appInfo.icon = applicationInfo.loadIcon(mPackageManager);
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
            mAppInfoListener.onSucceed(runningAppInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Need ROOT
     */
    public void getWifiPasswd() {
//        mAppInfoListener = listener;
        ShellUtils.ShellResult result = ShellUtils.execCmd("cat /data/misc/wifi/*.conf", true, true);
        if (result.errorMsg != null) {
            LogUtils.e(result.errorMsg);
            return;
        }
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
        Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");

        Matcher matcher = network.matcher(result.successMsg);
        List<WIFIInfo> infos = new ArrayList<>();
        while (matcher.find()) {
            String wifi = matcher.group();
            Matcher ssidMatcher = ssid.matcher(wifi);

            if (ssidMatcher.find()) {
                WIFIInfo info = new WIFIInfo();
                // Get SSID
                info.SSID = ssidMatcher.group(1);
                Matcher pskMatcher = psk.matcher(wifi);

                if (pskMatcher.find()) {
                    info.psk = pskMatcher.group(1);
                }
                infos.add(info);
                LogUtils.d(info.toString());
            }
        }
    }
}
