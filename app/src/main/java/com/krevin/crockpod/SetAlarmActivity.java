package com.krevin.crockpod;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetAlarmActivity extends Activity {

    private AlarmRepository mAlarmRepository;

    public static Intent getIntent(Context context) {
        return new Intent(context, SetAlarmActivity.class);
    }

    public static PendingIntent buildPendingIntent(Context context, int id, Intent intent) {
        return PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        mAlarmRepository = new AlarmRepository(this);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        final EditText podcastFeedField = (EditText) findViewById(R.id.podcast_rss_feed);
        Button setAlarmButton = (Button) findViewById(R.id.set_alarm);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm(timePicker.getHour(), timePicker.getMinute(), podcastFeedField.getText().toString());
                finish();
            }
        });
    }

    private void createAlarm(int hour, int minute, String podcastFeed) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Alarm alarm = new Alarm(podcastFeed, calendar, this);

        int requestId = mAlarmRepository.add(alarm);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                buildPendingIntent(this, requestId, alarm.getIntent())
        );
    }

}
