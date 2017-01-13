package com.fs.fs.utils;

import android.content.Context;

import java.io.File;
import java.util.List;

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

    public static void delete(File f) {
        if (f.exists() && f.isFile()) {
            boolean flag = f.delete();
        }
    }

    public static void delete(List<File> fileList) {
        if (fileList == null) {
            return;
        }
        for (File f : fileList) {
            if (f.exists() && f.isFile()) {
                boolean flag = f.delete();
            }
        }
    }
}
