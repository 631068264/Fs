package com.fs.fs.api.network.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;

/**
 * Created by wyx on 2017/1/8.
 */

public class OkHttpConfig {
    private volatile static OkHttpConfig mInstance;
    private OkHttpClient client;
    public static final long REQ_TIMEOUT = 10 * 1000;
    private String baseUrl;
    private List<Param> commonParams = new ArrayList<>();
    private Headers.Builder commonHeaders = new Headers.Builder();

    private OkHttpConfig(OkHttpClient client, String baseUrl) {
        if (client == null) {
            this.client = new OkHttpClient().newBuilder()
                    .readTimeout(REQ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(REQ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(REQ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
        } else {
            this.client = client;
        }
        this.baseUrl = TextUtils.isEmpty(baseUrl) ? "" : baseUrl;
    }

    public static OkHttpConfig getInstance() {
        return init(null, null);
    }

    public static OkHttpConfig init(OkHttpClient client, String baseUrl) {
        if (mInstance == null) {
            synchronized (OkHttpConfig.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpConfig(client, baseUrl);
                }
            }
        }
        return mInstance;
    }

    public OkHttpConfig updateCommonParams(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            commonParams.add(new Param(key, value));
        }
        return this;
    }

    public OkHttpConfig updateCommonHeaders(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            commonHeaders.add(key, value);
        }
        return this;
    }

    public List<Param> getCommonParams() {
        return commonParams;
    }

    public Headers.Builder getCommonHeaders() {
        return commonHeaders;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}

