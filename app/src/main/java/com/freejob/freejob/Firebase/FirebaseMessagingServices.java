package com.freejob.freejob.Firebase;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.freejob.freejob.Globals.Config;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleNotification(remoteMessage.getNotification().getBody(), remoteMessage.getTo());

    }

    private void handleNotification(String body, String to) {
        Intent pushNotification = new Intent(Config.STR_PUSH);
        pushNotification.putExtra("message",body);
        pushNotification.putExtra("to",to);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}
