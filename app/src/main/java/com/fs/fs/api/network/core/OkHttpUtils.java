package com.fs.fs.api.network.core;

import android.util.Log;

import com.fs.fs.App;
import com.fs.fs.api.network.core.callback.BaseCallback;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.fs.fs.api.network.core.BaseResponse.Status;


/**
 * Created by wyx on 2017/1/9.
 */

public class OkHttpUtils {
    // TODO:cancel the request and other Method
    private enum Method {
        GET, POST, PUT, DELETE, HEAD, PATCH,
    }

    public static void getAsync(String url, HttpParams params, BaseCallback callback) {
        url += OkHttpConfig.getInstance().getBaseUrl();
        request(Method.GET, url, params, false, callback);
    }

    public static void get(String url, HttpParams params, BaseCallback callback) {
        url += OkHttpConfig.getInstance().getBaseUrl();
        request(Method.GET, url, params, true, callback);
    }


    public static void get(String url, HttpParams params) {
        get(url, params, null);
    }

    public static void post(String url, HttpParams params, BaseCallback callback) {
        url += OkHttpConfig.getInstance().getBaseUrl();
        request(Method.POST, url, params, true, callback);
    }

    public static void postAsync(String url, HttpParams params, BaseCallback callback) {
        url += OkHttpConfig.getInstance().getBaseUrl();
        request(Method.POST, url, params, false, callback);
    }

    public static void post(String url, HttpParams params) {
        post(url, params, null);
    }

    private static void request(Method method, String url, HttpParams params, boolean isBlock, final BaseCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(App.getInstance())) {
            callback.onError("NetWork Failed");
            LogUtils.d("NetWork Failed");
            return;
        }
        String srcUrl = url;
        CallManager.getInstance().removeCall(url);
        Headers headers = params.getHeaders().build();
        Request.Builder builder = new Request.Builder();
        switch (method) {
            case GET:
                url += params.getParams();
                break;
            case POST:
                RequestBody body = params.getRequestBody();
                if (body != null) {
                    builder.post(body);
                }
                break;
        }
        Request request = builder.url(url).tag(srcUrl).headers(headers).build();
        Call call = OkHttpConfig.getInstance().getClient().newCall(request);
        CallManager.getInstance().addCall(srcUrl, call);
        LogUtils.d("Call: %s", url);
        try {
            if (isBlock) {
                try {
                    Response response = call.execute();
                    if (response.isSuccessful() && callback != null) {
                        parseResponse(callback, response);
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        LogUtils.e(Log.getStackTraceString(e));
                        callback.onError(Log.getStackTraceString(e));
                    }
                    e.printStackTrace();
                }
            } else {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtils.e(Log.getStackTraceString(e));
                        callback.onError(Log.getStackTraceString(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        parseResponse(callback, response);
                    }
                });
            }
        } catch (Exception e) {
            LogUtils.e(Log.getStackTraceString(e));
        }

    }

    private static void parseResponse(BaseCallback callback, Response response) {
        BaseResponse res = (BaseResponse) GsonUtils.getInstance().fromJson(response.body().charStream(), callback.getClazz());
        if (res == null) {
            LogUtils.e("Unknown Error : %d : %s:%s", response.code(), response.request().url(), response.body().toString());
        }
        switch (res.status) {
            case Status.SUCCESS:
                if (callback instanceof HttpCallback) {
                    ((HttpCallback) callback).onSuccess(res, response.headers());
                    LogUtils.d("%d:%s:%s", response.code(), response.request().url(), res.message);
                }
                break;
            case Status.FAIL:
                callback.onError(res.message);
                LogUtils.e(res.message);
                break;
        }
    }
}
