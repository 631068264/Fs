package com.fs.fs.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import com.fs.fs.App;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FileUtils;
import com.fs.fs.utils.LogUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by wyx on 2017/1/3.
 */

public class CameraService {

    private CameraManager mCameraManager = null;
    private Camera mCamera = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private CameraService() {
        this.mCameraManager = (CameraManager) App.getInstance().getSystemService(Context.CAMERA_SERVICE);
    }

    private static class SingletonHolder {
        private static final CameraService INSTANCE = new CameraService();
    }

    public static CameraService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void takePhoto(Integer cameraId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            takePhoto_high(String.valueOf(cameraId));
        } else {
            try {
                takePhoto_low(cameraId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void takePhoto_high(String cameraId) {
//        mCameraManager.openCamera(cameraId, );
    }

    private void takePhoto_low(Integer cameraId) throws IOException {
        cameraId = cameraId == null ? getCameraId_low() : cameraId;
        if (cameraId == -1) return;
        mCamera = Camera.open(cameraId);
        mCamera.setPreviewTexture(new SurfaceTexture(10));
        Camera.Parameters params = mCamera.getParameters();
        params.setJpegQuality(70);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        Size size = getOptimalSize(params.getSupportedPictureSizes());
        params.setPictureSize(size.width, size.height);
        mCamera.setParameters(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCamera.enableShutterSound(false);
        }
        mCamera.startPreview();
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String fileName = DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss");
                fileName = String.format("%s.%s", fileName, "jpg");
                FileOutputStream out = null;
                try {
                    String path = FileUtils.getExternalFullPath(App.getInstance(), fileName);
                    LogUtils.d(path);
                    out = new FileOutputStream(path);
                    out.write(data);
                    // TODO:上传并删除
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

    }

    /**
     * Get the smallest size
     *
     * @param supportedPictureSizes
     * @return
     */
    public static Size getOptimalSize(List<Size> supportedPictureSizes) {
        Size size = null;
        for (Size supportedSize : supportedPictureSizes) {
            if (size == null) {
                size = supportedSize;
            } else {
                if (supportedSize.width < size.width && supportedSize.height < size.height) {
                    size = supportedSize;
                }
            }
        }
        return size;
    }

    @Deprecated
    private String getCameraId_high() throws CameraAccessException {
        String backId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer face = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (face != null && face == CameraCharacteristics.LENS_FACING_FRONT)
                    return cameraId;
                else if (face != null && face == CameraCharacteristics.LENS_FACING_BACK) {
                    backId = cameraId;
                }
            }
        }
        return backId;
    }

    public static int getCameraId_low() {
        int backIndex = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int index = 0; index < cameraCount; index++) {
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return index;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = index;
            }
        }
        return backIndex;
    }


}
