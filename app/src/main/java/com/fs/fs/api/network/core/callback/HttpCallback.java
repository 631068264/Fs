package com.fs.fs.api.network.core.callback;

import com.fs.fs.api.network.core.BaseResponse;

import okhttp3.Headers;

/**
 * Created by wyx on 2017/1/10.
 */

public abstract class HttpCallback extends BaseCallback {

    public HttpCallback(Class clazz) {
        super(clazz);
    }

    public abstract void onSuccess(BaseResponse httpResponse, Headers headers);


}

