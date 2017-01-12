package com.fs.fs.api;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;

import com.fs.fs.App;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FileUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created by wyx on 2017/1/4.
 * <p>
 * DOC:https://developer.android.com/guide/topics/media/camera.html#capture-video
 */

public class MediaRecordService {
    private MediaRecorder mMediaRecorder;
    private Camera mCamera = null;
    private String path = null;
    private Boolean isStart = false;


    private MediaRecordService() {
    }

    private static class SingletonHolder {
        private static final MediaRecordService INSTANCE = new MediaRecordService();
    }

    public static MediaRecordService getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void startRecordAudio() {
        stopRecord();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioEncodingBitRate(96000);
        mMediaRecorder.setAudioSamplingRate(48 * 1000);
        mMediaRecorder.setAudioChannels(1);

        String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "3gp");
        path = FileUtils.getExternalFullPath(App.getInstance(), fileName);
        mMediaRecorder.setOutputFile(path);
        mMediaRecorder.setAudioChannels(1);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            stopRecord();
        }
        mMediaRecorder.start();
    }

    public void startRecordVideo() {
        stopRecord();
        try {
            /**
             TODO:WTF!!! record video will have a sound when MediaRecorder start or stop.
             But record audio no
             TODO:Use ffmpeg replace MediaRecorder
             **/
            //TODO:可以选镜头
            mCamera = Camera.open(CameraService.getCameraId_low());
            mCamera.setPreviewTexture(new SurfaceTexture(10));
            mMediaRecorder = new MediaRecorder();
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setAudioEncodingBitRate(960 * 1000);
            mMediaRecorder.setAudioSamplingRate(48 * 1000);
            mMediaRecorder.setAudioChannels(1);

            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoEncodingBitRate(300 * 1000);
            mMediaRecorder.setVideoSize(640, 480);

            String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "mp4");
            path = FileUtils.getExternalFullPath(App.getInstance(), fileName);
            mMediaRecorder.setOutputFile(path);

//            mMediaRecorder.setMaxDuration(1 * 1000);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isStart = true;
        } catch (Exception e) {
            e.printStackTrace();
            stopRecord();
        }
    }


    public void restartRecordAudio() {
        stop();
        startRecordAudio();
    }

    public void stopRecord() {
        releaseMediaRecorder();
        releaseCamera();
    }

    private void stop() {
        if (mMediaRecorder != null) {
            try {
                if (isStart) mMediaRecorder.stop();
                mMediaRecorder.reset();
                //TODO:删除
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMediaRecorder = null;
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            try {
                if (isStart) mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                //TODO:上传删除
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMediaRecorder = null;
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

}
