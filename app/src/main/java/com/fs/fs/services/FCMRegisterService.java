package com.fs.fs.services;

import android.text.TextUtils;

import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import okhttp3.Headers;

/**
 * Created by wyx on 2017/1/6.
 */

public class FCMRegisterService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LogUtils.d(refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (!TextUtils.isEmpty(refreshedToken)) {
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(String token) {
        SharePreferencesUtils.getInstance().put(Constant.SHARE_KEYS.FCM, token);
        OkHttpUtils.postAsync(ApiConfig.getFCMToken(), new HttpParams().addJson("token", token), new HttpCallback(BaseResponse.class) {
            @Override
            public void onSuccess(BaseResponse httpResponse, Headers headers) {
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

}
