package com.fs.fs.api.network.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

/**
 * Created by wyx on 2017/1/10.
 */

public class GsonUtils {
    private Gson gson;
    private Gson buidler;

    private GsonUtils() {
        this.gson = new Gson();
        this.buidler = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    private static class SingletonHolder {
        private static final GsonUtils INSTANCE = new GsonUtils();
    }

    public static GsonUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T fromJson(String json, Class<T> cls) {
        if (json == null) return null;
        try {
            return gson.fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public <T> T fromJson(Reader json, Class<T> cls) {
        if (json == null) return null;
        try {
            return gson.fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T fromJsonWithExopse(String json, Class<T> cls) {
        if (json == null) return null;
        try {
            return buidler.fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public <T> T fromJsonWithExopse(JsonElement json, Class<T> cls) {
        if (json == null) return null;
        try {
            return buidler.fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String toJsonWithExopse(Object obj) {
        return buidler.toJson(obj);
    }

}


