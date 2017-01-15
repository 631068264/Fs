package com.fs.fs.api.network.core;

import android.text.TextUtils;

import com.fs.fs.utils.EncodeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by wyx on 2017/1/9.
 */

public class HttpParams {
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
    private static final MediaType JSON_TYPE = MediaType.parse("application/jsonMap; charset=utf-8");

    private Headers.Builder headers = new Headers.Builder();
    private List<Param> params = new ArrayList<>();
    private List<Param> files = new ArrayList<>();
    private HashMap<String, Object> jsonMap = new HashMap<>();

    public HttpParams() {
        initCommon();
    }

    private void initCommon() {
        headers.add("charset", "UTF-8");
        List<Param> commonParams = OkHttpConfig.getInstance().getCommonParams();
        if (commonParams != null && commonParams.size() > 0) {
            params.addAll(commonParams);
        }

        Headers commonHeaders = OkHttpConfig.getInstance().getCommonHeaders().build();
        if (commonHeaders.size() > 0) {
            for (int i = 0; i < commonHeaders.size(); i++) {
                headers.add(commonHeaders.name(i), commonHeaders.value(i));
            }
        }
    }

    //============================= FormData =========================
    public HttpParams addParam(String key, String value) {
        if (value == null) {
            value = "";
        }
        Param param = new Param(key, value);
        if (!TextUtils.isEmpty(key) && !params.contains(param)) {
            params.add(new Param(key, value));
        }
        return this;
    }

    public HttpParams addParam(String key, int value) {
        return addParam(key, String.valueOf(value));
    }

    public HttpParams addParam(String key, long value) {
        return addParam(key, String.valueOf(value));
    }

    public HttpParams addParam(String key, float value) {
        return addParam(key, String.valueOf(value));
    }

    public HttpParams addParam(String key, double value) {
        return addParam(key, String.valueOf(value));
    }

    public HttpParams addParam(String key, boolean value) {
        return addParam(key, String.valueOf(value));
    }

    public HttpParams addParams(List<Param> params) {
        this.params.addAll(params);
        return this;
    }

    private boolean isFileEmpty(File file) {
        return (file == null || !file.exists() || file.length() == 0);
    }

    public HttpParams addFile(String key, File file) {
        if (isFileEmpty(file)) {
            return this;
        }
        if (!TextUtils.isEmpty(key)) {
            return this;
        }
        files.add(new Param(key, new FileParam(file)));
        return this;
    }

    public HttpParams addFiles(String key, List<File> files) {
        for (File file : files) {
            addFile(key, file);
        }
        return this;
    }

    //============================= header =========================

    public HttpParams addHeader(String key, String value) {
        if (value == null) {
            value = "";
        }
        if (!TextUtils.isEmpty(key)) {
            headers.add(key, value);
        }
        return this;
    }

    public HttpParams addHeader(String key, int value) {
        return addHeader(key, String.valueOf(value));
    }

    public HttpParams addHeader(String key, long value) {
        return addHeader(key, String.valueOf(value));
    }

    public HttpParams addHeader(String key, float value) {
        return addHeader(key, String.valueOf(value));
    }

    public HttpParams addHeader(String key, double value) {
        return addHeader(key, String.valueOf(value));
    }

    public HttpParams addHeader(String key, boolean value) {
        return addHeader(key, String.valueOf(value));
    }

    //============================= jsonMap =========================

    public HttpParams addJson(String key, Object object) {
        if (!TextUtils.isEmpty(key)) {
            jsonMap.put(key, object);
        }
        return this;
    }

    //============================= urlEn =========================

    //============================= get =========================

    public Headers.Builder getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        RequestBody body = null;
        if (jsonMap.size() > 0) {
            body = RequestBody.create(JSON_TYPE, GsonUtils.getInstance().toJson(jsonMap));
        } else if (files.size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Param param : files) {
                FileParam file = param.file;
                builder.addFormDataPart(param.key, file.fileName, RequestBody.create(FILE_TYPE, file.file));
            }
            for (Param param : params) {
                builder.addFormDataPart(param.key, param.value);
            }
            body = builder.build();
        } else if (params.size() > 0) {
            FormBody.Builder builder = new FormBody.Builder();
            for (Param param : params) {
                builder.add(param.key, param.value);
            }
            body = builder.build();
        }
        return body;
    }

    public String getParams() {
        String result = "";
        if (params.size() > 0) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("?");
            for (Param param : params) {
                param.key = EncodeUtils.urlDecode(param.key);
                param.value = EncodeUtils.urlDecode(param.value);
                buffer.append(param.key).append("=").append(param.value).append("&");
            }
            result = buffer.substring(0, buffer.length() - 1);
        }
        return result;
    }

}
