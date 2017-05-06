package com.krevin.crockpod.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmListIntent = AlarmListActivity.getIntent(context);
        alarmListIntent.putExtras(intent);
        alarmListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        new Alarm(context, intent).set();

        context.startActivity(alarmListIntent);
        setResultCode(Activity.RESULT_OK);
    }

}
