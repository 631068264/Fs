package com.fs.fs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.fs.fs.App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

//import org.apache.commons.codec.binary.Base64;


/**
 * @Annotation SharePreferences工具类
 */
public class SharePreferencesUtils {
    private static final String FILE_NAME = Constant.SHARE_FILE_NAME;

    private static SharedPreferences mSharedPreferences = null;
    private static SharedPreferences.Editor mEditor = null;


    @SuppressLint("CommitPrefEdits")
    private SharePreferencesUtils() {
        mSharedPreferences = App.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private static class SingletonHolder {
        private static final SharePreferencesUtils INSTANCE = new SharePreferencesUtils();
    }

    public static SharePreferencesUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void put(String key, Object value) {
        if (value instanceof String) {
            mEditor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            mEditor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            mEditor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else {
            serialize(key, value);
        }
        mEditor.apply();
    }

    /**
     * 序列化
     *
     * @param key
     * @param bean
     */
    private static void serialize(String key, Object bean) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(bean);
            String base = EncodeUtils.base64Encode(baos.toByteArray());
            mEditor.putString(key, base);
            oos.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return mSharedPreferences.getString(key, String.valueOf(defaultValue));
        } else if (defaultValue instanceof Integer) {
            return mSharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            return mSharedPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            return mSharedPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return mSharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else {
            String base = mSharedPreferences.getString(key, null);
            if (base == null) {
                return defaultValue;
            }
            try {
                byte[] base64 = EncodeUtils.base64Decode(base);
                ByteArrayInputStream bais = new ByteArrayInputStream(base64);
                ObjectInputStream bis = new ObjectInputStream(bais);
                Object object = bis.readObject();
                bais.close();
                bis.close();
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
    }


    public void remove(String key) {
        mEditor.remove(key);
        mEditor.apply();
    }

    public void clear() {
        mEditor.clear();
        mEditor.apply();
    }

    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }


}
