package com.fs.fs.utils;

import android.text.TextUtils;
import android.util.Log;

import com.fs.fs.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @Annotation Log工具
 */
public class LogUtils {


    enum LogLevel {
        FULL, NONE
    }

    private static LogLevel getLogLevel() {
        return BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE;
    }

    private static String getTag() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        //String threadName = Thread.currentThread().getName();
        String tag = "Logger";
        for (int i = 1; i < trace.length; i++) {
            if (trace[i - 1].getClassName().equals(LogUtils.class.getName())
                    && !trace[i].getClassName().equals(LogUtils.class.getName())) {
                tag = trace[i].getClassName().replaceAll("^.*\\.", "");
                break;
            }
        }
        return tag;
    }

    /**
     * LogUtils.d("hello %s %d", "world", 5);
     * LogUtils.d("Hello world")
     *
     * @param message
     * @param args
     */
    public static void d(Object message, Object... args) {
        log(Constant.DEBUG, message, args);
    }

    public static void e(Object message, Object... args) {
        log(Constant.ERROR, message, args);
    }

    public static void e(Throwable throwable) {
        log(Constant.ERROR, Log.getStackTraceString(throwable));
    }

    public static void i(Object message, Object... args) {
        log(Constant.INFO, message, args);
    }

    public static void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
        } else {
            try {
                String message;
                if (json.startsWith("{")) {
                    JSONObject e1 = new JSONObject(json);
                    message = e1.toString(4);
                    d(message);
                    return;
                }
                if (json.startsWith("[")) {
                    JSONArray e = new JSONArray(json);
                    message = e.toString(4);
                    d(message);
                }
            } catch (JSONException exception) {
                e(exception.getCause().getMessage() + "\n" + json);
            }

        }
    }

    public static void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
        } else {
            try {
                StreamSource e = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(e, xmlOutput);
                d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } catch (TransformerException exception) {
                e(exception.getCause().getMessage() + "\n" + xml);
            }

        }
    }

    private static void log(int logType, Object msg, Object... args) {
        String m = String.valueOf(msg);
        if (getLogLevel() != LogLevel.NONE && !TextUtils.isEmpty(m)) {
            String tag = getTag();
            String message = getMessage(m, args);
            switch (logType) {
                case Constant.DEBUG:
                    Log.d(tag, message);
                    break;
                case Constant.ERROR:
                default:
                    Log.e(tag, message);
                    break;
                case Constant.INFO:
                    Log.i(tag, message);
                    break;
            }
        }

    }


    private static String getMessage(String msg, Object... args) {
        return args.length == 0 ? msg : String.format(msg, args);
    }


}
