package com.krevin.crockpod;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;

import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.util.List;

public class MediaPlayerService extends Service implements Callback, MediaController.MediaPlayerControl {

    private static final int ALARM_NOTIFICATION_ID = 97;
    private static final String TAG = MediaPlayerService.class.getCanonicalName();

    private final IBinder mBinder = new MediaPlayerBinder();
    private MediaPlayer mMediaPlayer;
    private Alarm mAlarm;

    public static Intent getIntent(Context context) {
        return new Intent(context, MediaPlayerService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String alarmId = intent.getStringExtra(Alarm.ALARM_ID_KEY);
        mAlarm = new AlarmRepository(this).find(alarmId);

        Log.d(TAG, "starting notification");
        Notification notification = NotificationSetup.createNotification(this, mAlarm.getPodcast().getName());
        startForeground(ALARM_NOTIFICATION_ID, notification);

        requestRssFeedAsync();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        stopSelf();
    }

    private void requestRssFeedAsync() {
        Log.d(TAG, mAlarm.getIntent().getExtras().toString());

        PkRSS.with(this)
                .load(mAlarm.getPodcast().getRssFeedUrl())
                .callback(this)
                .async();
    }

    private void blastTheAlarm(String mediaUrl) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build());

        try {
            mMediaPlayer.setDataSource(mediaUrl);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error playing media from URL: %s", mediaUrl));
        }

        setAlarmToMaxVolume();
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(mp -> start());
    }

    private void setAlarmToMaxVolume() {
        AudioManager systemService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamMaxVolume = systemService.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        systemService.setStreamVolume(AudioManager.STREAM_ALARM, streamMaxVolume, AudioManager.FLAG_SHOW_UI);
    }

    public class MediaPlayerBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
