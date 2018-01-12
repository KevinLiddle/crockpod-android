package com.krevin.crockpod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

public class NotificationSetup extends ContextWrapper {

    private static final String CHANNEL_ID = "crockpod_1";
    private static final int ALARM_NOTIFICATION_ID = 33;

    public NotificationSetup(Context base) {
        super(base);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        mChannel.setDescription("The place where your CrockPod alarms go off");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(false);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    public void createNotification(String podcastName) {
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);

        builder.setPriority(Notification.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_crockpod_mascot)
                .setContentText(podcastName);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }
}
