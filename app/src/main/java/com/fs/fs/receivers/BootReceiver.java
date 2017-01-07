package com.fs.fs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fs.fs.services.AppService;
import com.fs.fs.utils.NetworkUtils;

/**
 * Created by wyx on 2017/1/7.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                context.startService(new Intent(context, AppService.class));
            }
        }
    }
}
