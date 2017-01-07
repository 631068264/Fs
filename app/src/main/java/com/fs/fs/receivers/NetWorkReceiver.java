package com.fs.fs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.fs.fs.utils.NetworkUtils;

;

/**
 * Created by wyx on 2017/1/6.
 */

public class NetWorkReceiver extends BroadcastReceiver {
    public static final String NETWORK_AVAILABLE = "network_available";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NETWORK_AVAILABLE));
        }
    }

}
