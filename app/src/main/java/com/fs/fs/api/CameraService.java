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

    private Context mContext = null;
    private CameraManager mCameraManager = null;
    private Camera mCamera = null;

    public CameraService(Context context) {
        this.mContext = context;
        this.mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public void takePhoto() throws Exception {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String cameraId = getCameraId_high();
            if (cameraId == null) {
                throw new Exception("You don't have any camera on your phone");
            }
            takePhoto_high(cameraId);
        } else {
            int cameraId = getCameraId_low();
            if (cameraId == -1) {
                throw new Exception("You don't have any camera on your phone");
            }
            takePhoto_low(cameraId);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void takePhoto_high(String cameraId) {
//        mCameraManager.openCamera(cameraId, );
    }

    private void takePhoto_low(int cameraId) throws IOException {
        mCamera = Camera.open(cameraId);
        mCamera.setPreviewTexture(new SurfaceTexture(10));
        Camera.Parameters params = mCamera.getParameters();
        params.setJpegQuality(70);
//        params.setPreviewSize(640, 480);
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
                    String path = FileUtils.getExternalFullPath(mContext, fileName);
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
    private Size getOptimalSize(List<Size> supportedPictureSizes) {
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

    private int getCameraId_low() {
        int backIndex = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = cameraCount = Camera.getNumberOfCameras();
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
