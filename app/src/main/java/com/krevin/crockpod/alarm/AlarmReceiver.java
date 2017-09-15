package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.krevin.crockpod.alarm.repositories.AlarmRepository;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmListIntent = AlarmRingingActivity.getIntent(context);
        alarmListIntent.putExtras(intent);
        alarmListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);

        Log.d("WATWAT", "receiving alarm");
        Alarm alarm = new Alarm(intent);
        new AlarmRepository(context).set(new Alarm(intent, alarm.getNextTriggerTime().getHourOfDay(), alarm.getNextTriggerTime().getMinuteOfHour()));

        context.startActivity(alarmListIntent);
    }
}
