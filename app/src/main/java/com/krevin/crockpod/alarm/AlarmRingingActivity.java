package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.NotificationSetup;
import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.util.List;


public class AlarmRingingActivity extends CrockpodActivity implements Callback, MediaController.MediaPlayerControl {

    private static final String TAG = AlarmRingingActivity.class.getCanonicalName();

    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmRingingActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        Button cancelButton = findViewById(R.id.alarm_cancel);
        cancelButton.setOnClickListener(v -> {
            stopPlaying();
            finish();
            startActivity(AlarmListActivity.getIntent(this));
        });

        String alarmId = getIntent().getStringExtra(Alarm.ALARM_ID_KEY);
        Alarm alarm = new AlarmRepository(this).find(alarmId);

        ImageLoader imageLoader = getCrockpodApp().getHttpClient().getImageLoader();
        NetworkImageView mPodcastAlarmLogoView = findViewById(R.id.podcast_alarm_logo);
        mPodcastAlarmLogoView.setImageUrl(alarm.getPodcast().getLogoUrlLarge(), imageLoader);
        TextView podcastAlarmTitle = findViewById(R.id.alarm_text);
        podcastAlarmTitle.setText(alarm.getPodcast().getName());

        mMediaController = new MediaController(this);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mPodcastAlarmLogoView);

        mPodcastAlarmLogoView.setOnTouchListener((v, m) -> {
            mMediaController.show();
            return true;
        });

        new NotificationSetup(this).createNotification(alarm.getPodcast().getName());
        requestRssFeedAsync(alarm);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
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

    private void stopPlaying() {
        mMediaController.hide();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
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
        mMediaPlayer.setOnPreparedListener(mp -> {
            this.start();
            mMediaController.setEnabled(true);
            mMediaController.show(0);
        });
    }

    private void setAlarmToMaxVolume() {
        AudioManager systemService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamMaxVolume = systemService.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        systemService.setStreamVolume(AudioManager.STREAM_ALARM, streamMaxVolume, AudioManager.FLAG_SHOW_UI);
    }
}
