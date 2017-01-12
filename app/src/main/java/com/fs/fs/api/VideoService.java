package com.fs.fs.api;

import com.fs.fs.App;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FileUtils;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.util.Date;

;


/**
 * Created by wyx on 2017/1/11.
 */
@Deprecated
public class VideoService {
    private FFmpegFrameRecorder mFrameRecorder;
    private String path;
    private OpenCVFrameGrabber grabber;

    private VideoService() {
    }

    private static class SingletonHolder {
        private static final VideoService INSTANCE = new VideoService();
    }

    public static VideoService getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void startRecordVideo() {
        grabber = new OpenCVFrameGrabber(1);
        grabber.setImageWidth(640);
        grabber.setImageHeight(480);
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "mp4");
        path = FileUtils.getExternalFullPath(App.getInstance(), fileName);
        mFrameRecorder = new FFmpegFrameRecorder(path, 640, 480, 1);
        mFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        mFrameRecorder.setVideoOption("tune", "zerolatency");
        mFrameRecorder.setVideoOption("preset", "ultrafast");
        mFrameRecorder.setVideoOption("crf", "28");
        mFrameRecorder.setVideoBitrate(300 * 1000);
        mFrameRecorder.setFormat("mp4");

        mFrameRecorder.setFrameRate(30);
        mFrameRecorder.setAudioOption("crf", "0");
        mFrameRecorder.setSampleRate(48 * 1000);
        mFrameRecorder.setAudioBitrate(960 * 1000);
        mFrameRecorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        try {
            mFrameRecorder.start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//
//                }
//            }).start();
            mFrameRecorder.record(grabber.grab());
        } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mFrameRecorder != null) {
            try {
                mFrameRecorder.stop();
                mFrameRecorder.release();
                grabber.stop();
            } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
                e.printStackTrace();
            }
            mFrameRecorder = null;
            grabber = null;
        }
    }

}

