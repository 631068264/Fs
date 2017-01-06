package com.fs.fs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

;

/**
 * Created by wyx on 2017/1/6.
 */

public class NetWorkReceiver extends BroadcastReceiver {
    public static final String NETWORK_AVAILABLE = "network_available";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (networkAvailable(context)) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NETWORK_AVAILABLE));
        }
    }

    private static Boolean networkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                return true;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return true;
            }
            return false;
        }
        return false;
    }
}
