package com.krevin.crockpod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.krevin.crockpod.alarm.AlarmRingingActivity;

public class NotificationSetup {

    private static final String CHANNEL_ID = "crockpod_1";

    public static Notification createNotification(Context context, String podcastName) {
        Intent intent = AlarmRingingActivity.getIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CrockPod notification channel", importance);
        mChannel.setDescription("For CrockPod alarms");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(false);
        mNotificationManager.createNotificationChannel(mChannel);

        return new Notification.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(podcastName)
                .setSmallIcon(R.drawable.ic_crockpod_mascot)
                .build();
    }
}
