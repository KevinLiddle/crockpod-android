package com.krevin.crockpod.alarm;

import android.app.Activity;
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

import com.krevin.crockpod.R;
import com.krevin.crockpod.UniqueIntId;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.util.List;


public class AlarmRingingActivity extends Activity implements Callback {

    private static final String TAG = AlarmRingingActivity.class.getCanonicalName();

    private MediaPlayer mMediaPlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmRingingActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        Button cancelButton = (Button) findViewById(R.id.alarm_cancel);
        cancelButton.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            startActivity(AlarmListActivity.getIntent(this));
        });

        showAlarmNotification();
        requestRssFeedAsync();
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

    private void requestRssFeedAsync() {
        Alarm alarm = new Alarm(this, getIntent());
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

        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());
    }

    private void showAlarmNotification() {
        Notification.Builder builder = new Notification.Builder(this);

        builder.setPriority(Notification.PRIORITY_MAX).
                setCategory(Notification.CATEGORY_ALARM).
                setContentTitle(getString(R.string.app_name)).
                setSmallIcon(R.drawable.ic_crockpod_logo).
                setStyle(new Notification.BigTextStyle().bigText(getString(R.string.alarm_notification_text)));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(UniqueIntId.generate(this), builder.build());
    }
}
