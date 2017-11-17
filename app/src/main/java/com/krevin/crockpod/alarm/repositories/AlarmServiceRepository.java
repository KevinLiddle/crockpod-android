package com.krevin.crockpod.alarm.repositories;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.alarm.AlarmReceiver;

class AlarmServiceRepository {

    private static final int ALARM_REQUEST_CODE = 72;

    private final Context mContext;
    private final AlarmManager mAlarmManager;

    AlarmServiceRepository(Context context) {
        mContext = context.getApplicationContext();
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    void set(Alarm alarm) {
        mAlarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarm.getNextTriggerTime().getMillis(),
                buildPendingIntent(alarm));
    }

    void cancel(Alarm alarm) {
        mAlarmManager.cancel(buildPendingIntent(alarm));
    }

    private PendingIntent buildPendingIntent(Alarm alarm) {
        Intent intent = new Intent(mContext, AlarmReceiver.class)
                .putExtra(Alarm.ALARM_ID_KEY, alarm.getId().toString());

        return PendingIntent.getBroadcast(
                mContext,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
