package com.krevin.crockpod.alarm.repositories;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.krevin.crockpod.alarm.Alarm;

public class AlarmServiceRepository {

    private final Context mContext;
    private final AlarmManager mAlarmManager;

    public AlarmServiceRepository(Context context) {
        mContext = context.getApplicationContext();
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void set(Alarm alarm) {
        mAlarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarm.getNextTriggerTime().getMillis(),
                buildPendingIntent(alarm)
        );
    }

    public void cancel(Alarm alarm) {
        mAlarmManager.cancel(buildPendingIntent(alarm));
    }

    private PendingIntent buildPendingIntent(Alarm alarm) {
        return PendingIntent.getBroadcast(
                mContext,
                alarm.getId(),
                alarm.getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
