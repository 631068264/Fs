package com.fs.fs.api.network.core;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by wyx on 2017/1/9.
 */

public class Param {
    public String key = "";
    public String value = "";
    public FileParam file = null;

    public Param(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Param(String key, File file) {
        this.key = key;
        this.file = new FileParam(file);
    }

    public Param() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Param)) {
            return false;
        }
        Param part = (Param) obj;
        return TextUtils.equals(part.key, this.key);
    }
}
