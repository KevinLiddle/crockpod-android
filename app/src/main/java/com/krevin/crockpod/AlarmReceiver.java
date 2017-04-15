package com.krevin.crockpod;

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
        alarmListIntent.putExtra(AlarmListActivity.ALARM_RINGING_KEY, true);
        alarmListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alarmListIntent);
        setResultCode(Activity.RESULT_OK);
    }

}
