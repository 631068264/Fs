package com.fs.fs.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by wyx on 2017/1/4.
 */

public class FileUtils {
    public static String getExternalFullPath(Context context, String fileName, String env) {
        File dir = context.getExternalFilesDir(env);
        if (dir == null) {
            return getFullPath(context, fileName);
        }
        return dir.getAbsolutePath() + File.separator + fileName;
    }

    public static String getExternalFullPath(Context context, String fileName) {
        return getExternalFullPath(context, fileName, null);
    }

    public static String getFullPath(Context context, String fileName) {
        return context.getFilesDir().getAbsolutePath() + "/" + fileName;
    }

    public static String getApplicationRoot(Context context) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            return context.getFilesDir().getAbsolutePath() + File.separator;
        }
        return dir.getAbsolutePath() + File.separator;
    }
}
