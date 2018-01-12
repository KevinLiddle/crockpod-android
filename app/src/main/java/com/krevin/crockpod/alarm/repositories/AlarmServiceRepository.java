package com.krevin.crockpod.alarm.repositories;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.alarm.AlarmReceiver;

class AlarmServiceRepository {

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
                buildPendingIntent(alarm)
        );
    }

    void cancel(Alarm alarm) {
        mAlarmManager.cancel(buildPendingIntent(alarm));
    }

    private PendingIntent buildPendingIntent(Alarm alarm) {
        Intent intent = AlarmReceiver.getIntent(mContext)
                .putExtra(Alarm.ALARM_ID_KEY, alarm.getId().toString());

        return PendingIntent.getBroadcast(
                mContext,
                alarm.getId().hashCode(),
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }
}
