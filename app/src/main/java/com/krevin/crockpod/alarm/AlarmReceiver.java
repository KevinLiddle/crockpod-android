package com.krevin.crockpod.alarm;

import android.app.Activity;
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
        Intent alarmListIntent = AlarmListActivity.getIntent(context);
        alarmListIntent.putExtras(intent);
        alarmListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);

        new AlarmRepository(context).set(new Alarm(context, intent));

        context.startActivity(alarmListIntent);
        setResultCode(Activity.RESULT_OK);
    }

}
