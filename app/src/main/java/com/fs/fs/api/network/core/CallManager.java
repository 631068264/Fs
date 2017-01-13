package com.fs.fs.api.network.core;

import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

/**
 * Created by wyx on 2017/1/10.
 */

public class CallManager {
    private ConcurrentHashMap<String, Call> callMap;

    private CallManager() {
        this.callMap = new ConcurrentHashMap<>();
    }

    private static class SingletonHolder {
        private static final CallManager INSTANCE = new CallManager();
    }

    public static CallManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addCall(String url, Call call) {
        if (call != null && !TextUtils.isEmpty(url)) {
            callMap.put(url, call);
        }
    }

    public void removeCall(String url) {
        if (!TextUtils.isEmpty(url)) {
            Call call = callMap.get(url);
            if (call != null) {
                call.cancel();
                callMap.remove(url);
            }
        }
    }
}
