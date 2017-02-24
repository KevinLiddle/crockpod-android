package com.krevin.crockpod;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.Calendar;

public class SetAlarmActivity extends Activity {

    public static final String TAG = SetAlarmActivity.class.getCanonicalName();
    public static final String PODCAST_FEED_KEY = "podcast_feed";
    public static final String ALARM_RINGING_KEY = "alarm_ringing";
    private MediaPlayer mediaPlayer;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SetAlarmActivity.class);
        intent.setFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        final EditText podcastFeedField = (EditText) findViewById(R.id.podcast_rss_feed);
        Button setAlarmButton = (Button) findViewById(R.id.set_alarm);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm(timePicker.getHour(), timePicker.getMinute(), podcastFeedField.getText().toString());
            }
        });

        showAlarmScreen();
    }

    private void createAlarm(int hour, int minute, String podcastFeed) {
        Intent intent = AlarmReceiver.getIntent(this);
        intent.putExtra(PODCAST_FEED_KEY, podcastFeed);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                R.id.alarm_request,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void showAlarmScreen() {
        final View alarmMessage = findViewById(R.id.alarm_message);
        Button cancelButton = (Button) findViewById(R.id.alarm_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                alarmMessage.setVisibility(View.INVISIBLE);
            }
        });

        if (getIntent().getBooleanExtra(ALARM_RINGING_KEY, false)) {
            alarmMessage.setVisibility(View.VISIBLE);
            showAlarmNotification();
            blastTheAlarm();
        }
    }

    private void blastTheAlarm() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        String mediaUrl = getIntent().getStringExtra(PODCAST_FEED_KEY);

        try {
            mediaPlayer.setDataSource(mediaUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(TAG, String.format("Error playing media from URL: %s", mediaUrl));
        }

        mediaPlayer.start();
    }

    private void showAlarmNotification() {
        Notification.Builder builder = new Notification.Builder(this);

        builder.setPriority(Notification.PRIORITY_MAX).
                setCategory(Notification.CATEGORY_ALARM).
                setContentTitle(getString(R.string.app_name)).
                setSmallIcon(R.drawable.ic_crockpod_logo).
                setStyle(new Notification.BigTextStyle().bigText(getString(R.string.alarm_notification_text)));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.alarm_notification, builder.build());
    }
}
