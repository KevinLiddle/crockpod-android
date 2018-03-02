package com.krevin.crockpod.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.MediaPlayerService;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;

public class AlarmReceiver extends BroadcastReceiver {

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmId = intent.getStringExtra(Alarm.ALARM_ID_KEY);

        AlarmRepository alarmRepository = new AlarmRepository(context.getApplicationContext());
        Alarm alarm = alarmRepository.find(alarmId);
        alarmRepository.set(alarm.buildNextAlarm());

        Intent mediaPlayerServiceIntent = MediaPlayerService.getIntent(context.getApplicationContext());
        mediaPlayerServiceIntent.putExtras(alarm.getIntent());
        context.startForegroundService(mediaPlayerServiceIntent);
    }
}
