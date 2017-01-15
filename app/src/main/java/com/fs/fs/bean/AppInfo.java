package com.fs.fs.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wyx on 2016/12/28.
 */

public class AppInfo implements Serializable{
    @SerializedName("app_name") public String appName ;
    @SerializedName("package_name")public String packageName ;
    @SerializedName("version_name")public String versionName ;
    @SerializedName("install_time")public String installTime ;
    @SerializedName("run_time")public String runningTime ;
    @SerializedName("is_foreground")public  boolean  isForeground;
}
