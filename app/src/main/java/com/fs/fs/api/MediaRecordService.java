package com.fs.fs.api;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaRecorder;

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
    private Context mContext = null;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera = null;
    private String path = null;
    private Boolean isStart = false;

    public MediaRecordService(Context context) {
        this.mContext = context;
    }

    public void startRecordAudio() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioEncodingBitRate(96000);
        mMediaRecorder.setAudioSamplingRate(48 * 1000);
        String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "3gp");
        path = FileUtils.getExternalFullPath(mContext, fileName);
        mMediaRecorder.setOutputFile(path);
        mMediaRecorder.setAudioChannels(1);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
        }
        mMediaRecorder.start();
    }

    public void startRecordVideo() {
        try {
            /**
             TODO:WTF!!! record video will have a sound when MediaRecorder start or stop.
             But record audio no
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
            mMediaRecorder.setAudioEncodingBitRate(96000);
            mMediaRecorder.setAudioSamplingRate(48 * 1000);

            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoSize(640, 480);

            String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "mp4");
            path = FileUtils.getExternalFullPath(mContext, fileName);
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

    private void setMuteAll(boolean mute) {
        AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        int[] streams = new int[]{
                AudioManager.STREAM_ALARM,
                AudioManager.STREAM_DTMF,
                AudioManager.STREAM_MUSIC,
                AudioManager.STREAM_RING,
                AudioManager.STREAM_SYSTEM,
                AudioManager.STREAM_VOICE_CALL
        };

        for (int stream : streams)
            manager.setStreamMute(stream, mute);
    }
}
