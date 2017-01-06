package com.fs.fs.service;

import com.fs.fs.utils.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by wyx on 2017/1/6.
 */

public class FCMMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            LogUtils.d("Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            LogUtils.d("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //TODO: FCM control
    }
}