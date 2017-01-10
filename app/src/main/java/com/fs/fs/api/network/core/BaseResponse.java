package com.fs.fs.api.network.core;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyx on 2017/1/10.
 */

public class BaseResponse {
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;


    public static final class Status {
        public static final int FAIL = 0;
        public static final int SUCCESS = 1;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("BaseResponse{status=%d, message='%s'}", status, message);
    }


}

