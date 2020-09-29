package com.learning.java.app.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.learning.java.app.R;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() == null) {
            return;
        }

        if(remoteMessage.getData().isEmpty())
        {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }else {
            showNotification(remoteMessage.getData());
        }

    }

    private void showNotification(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.anna.license.utilities";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Learning Java Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationCompat.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.java_icon_low)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("info");

        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notificationCompat.build());
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.anna.license.utilities";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Learning Java Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationCompat.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.java_icon_low)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("info");

        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notificationCompat.build());
        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
