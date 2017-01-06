package com.fs.fs.utils;

import com.fs.fs.App;
import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.loader.LoadJNI;

/**
 * Created by wyx on 2017/1/6.
 */

public class FFmpegUtils {
    private static LoadJNI ffmpeg = null;
    private static String logFolder = null;

    private FFmpegUtils() {
        ffmpeg = new LoadJNI();
        logFolder = FileUtils.getApplicationRoot(App.getInstance());
    }

    private static class SingletonHolder {
        private static final FFmpegUtils INSTANCE = new FFmpegUtils();
    }

    public static FFmpegUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void runCmd(String cmd) {
        run(cmd);
    }

    public void compressVideo(String srcfileName, String tarfileName) {
        String cmd = String.format("ffmpeg -y -i %s -s 640x480 -strict -2 -c:v mpeg4 -c:a aac %s", srcfileName, tarfileName);
        run(cmd);
    }

    private void run(String cmd) {
        try {
            ffmpeg.run(cmd.split(" "), logFolder, App.getInstance());
        } catch (CommandValidationException e) {
            e.printStackTrace();
        }
    }
}
