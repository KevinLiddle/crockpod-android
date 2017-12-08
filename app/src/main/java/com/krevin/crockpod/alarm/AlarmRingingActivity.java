package com.krevin.crockpod.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.util.List;


public class AlarmRingingActivity extends CrockpodActivity implements Callback {

    private static final String TAG = AlarmRingingActivity.class.getCanonicalName();
    private static final int ALARM_NOTIFICATION_ID = 33;

    private MediaPlayer mMediaPlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmRingingActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        Button cancelButton = findViewById(R.id.alarm_cancel);
        cancelButton.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            finish();
            startActivity(AlarmListActivity.getIntent(this));
        });

        showAlarmNotification();
        String alarmId = getIntent().getStringExtra(Alarm.ALARM_ID_KEY);
        Alarm alarm = new AlarmRepository(this).find(alarmId);
        requestRssFeedAsync(alarm);
    }

    @Override
    public void onPreload() {}

    @Override
    public void onLoaded(List<Article> newArticles) {
        String mediaUrl = newArticles.get(0).getEnclosure().getUrl();
        blastTheAlarm(mediaUrl);
    }

    @Override
    public void onLoadFailed() {
        Log.e(TAG, "OOPS! Couldn't fetch the RSS feed!");
    }

    private void requestRssFeedAsync(Alarm alarm) {
        Log.d(TAG, alarm.getIntent().getExtras().toString());

        PkRSS.with(this)
                .load(alarm.getPodcast().getRssFeedUrl())
                .callback(this)
                .async();
    }

    private void blastTheAlarm(String mediaUrl) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

        try {
            mMediaPlayer.setDataSource(mediaUrl);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error playing media from URL: %s", mediaUrl));
        }

        setAlarmToMaxVolume();
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());
    }

    private void showAlarmNotification() {
        Notification.Builder builder = new Notification.Builder(this);

        builder.setPriority(Notification.PRIORITY_MAX).
                setCategory(Notification.CATEGORY_ALARM).
                setContentTitle(getString(R.string.app_name)).
                setSmallIcon(R.drawable.ic_crockpod_mascot).
                setStyle(new Notification.BigTextStyle().bigText(getString(R.string.alarm_notification_text)));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

    private void setAlarmToMaxVolume() {
        AudioManager systemService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamMaxVolume = systemService.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        systemService.setStreamVolume(AudioManager.STREAM_ALARM, streamMaxVolume, AudioManager.FLAG_SHOW_UI);
    }
}
