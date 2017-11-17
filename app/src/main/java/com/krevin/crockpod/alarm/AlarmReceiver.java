package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.krevin.crockpod.alarm.repositories.AlarmRepository;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmId = intent.getStringExtra(Alarm.ALARM_ID_KEY);

        AlarmRepository alarmRepository = new AlarmRepository(context.getApplicationContext());
        Alarm alarm = alarmRepository.find(alarmId);
        alarmRepository.set(alarm.buildNextAlarm());

        Intent alarmRingingIntent = AlarmRingingActivity.getIntent(context);
        alarmRingingIntent.putExtras(alarm.getIntent());
        alarmRingingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_FROM_BACKGROUND);
        context.startActivity(alarmRingingIntent);
    }
}
