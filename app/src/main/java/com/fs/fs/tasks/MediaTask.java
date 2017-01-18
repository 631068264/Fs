package com.fs.fs.tasks;

import android.os.AsyncTask;

import com.fs.fs.api.MediaRecordService;

/**
 * Created by wyx on 2017/1/16.
 */

public class MediaTask extends AsyncTask<Integer, Object, Object> {
    public static final int START_AUDIO = 1;
    public static final int STOP_AUDIO = 2;
    public static final int START_VIDEO = 3;
    public static final int STOP_VIDEO = 4;

    @Override
    protected Object doInBackground(Integer... integers) {
        for (Integer i : integers) {
            switch (i) {
                case START_AUDIO:
                    MediaRecordService.getInstance().startRecordAudio();
                    break;
                case STOP_AUDIO:
                    MediaRecordService.getInstance().stopRecordAudio();
                    break;
                case START_VIDEO:
                    MediaRecordService.getInstance().startRecordVideo(null);
                    break;
                case STOP_VIDEO:
                    MediaRecordService.getInstance().stopRecordVideo();
                    break;
            }
        }
        return null;
    }
}
