package com.fs.fs.api.network.core.callback;

import com.fs.fs.api.network.core.BaseResponse;

import java.lang.reflect.ParameterizedType;

/**
 * Created by wyx on 2017/1/10.
 */

public abstract class BaseCallback<T extends BaseResponse> {
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public BaseCallback() {
        this.clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public abstract void onError(String errorMsg);

}
