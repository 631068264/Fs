package com.fs.fs.services;

import com.fs.fs.api.CmdThread;
import com.fs.fs.utils.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by wyx on 2017/1/6.
 */

public class FCMMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LogUtils.d("Message data payload: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("cmd")) {
            //FCM data control
            CmdThread cmd = new CmdThread(remoteMessage.getData());
            if (cmd.isCmdAvailable()) {
                new Thread(cmd).start();
            }
        }
        if (remoteMessage.getNotification() != null) {
            LogUtils.d("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}