package com.fs.fs.api.network.core.callback;

/**
 * Created by wyx on 2017/1/10.
 */

public abstract class BaseCallback {
    private Class clazz;

    @SuppressWarnings("unchecked")
    public BaseCallback(Class clazz) {
        this.clazz = clazz;
//        this.clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class getClazz() {
        return clazz;
    }

    public abstract void onError(String errorMsg);

}
