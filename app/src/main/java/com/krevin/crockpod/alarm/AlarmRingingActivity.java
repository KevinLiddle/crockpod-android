package com.krevin.crockpod.alarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.MediaPlayerService;
import com.krevin.crockpod.R;


public class AlarmRingingActivity extends CrockpodActivity {

    private static final String TAG = AlarmRingingActivity.class.getCanonicalName();

    private View mPodcastLogoWrapperView;
    private NetworkImageView mPodcastAlarmLogoView;
    private MediaController mMediaController;
    private MediaPlayerService mService;
    private boolean mBound = false;
    private Handler mHandler = new Handler();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "connected to the service");
            MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
            mService = binder.getService();
            mBound = true;
            setupViewsAndMediaController();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "unbound from service");
            mBound = false;
        }
    };

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmRingingActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "creating the activity");
        setContentView(R.layout.activity_alarm_ringing);

        mPodcastAlarmLogoView = findViewById(R.id.podcast_alarm_logo);
        mPodcastLogoWrapperView = findViewById(R.id.podcast_logo_wrapper);

        Button cancelButton = findViewById(R.id.alarm_cancel);
        cancelButton.setOnClickListener(v -> stopPlaying());
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "is it bound? " + mBound);
        if (!mBound) {
            Intent intent = MediaPlayerService.getIntent(getApplicationContext());
            bindService(intent, mConnection, 0);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "hiding media controller and unbinding service");
        mMediaController.hide();
        unbindService(mConnection);
    }

    private void setupViewsAndMediaController() {
        Alarm alarm = mService.getAlarm();
        Log.d(TAG, "setting up alarm: " + alarm);

        ImageLoader imageLoader = getCrockpodApp().getHttpClient().getImageLoader();
        mPodcastAlarmLogoView.setImageUrl(alarm.getPodcast().getLogoUrlLarge(), imageLoader);

        Log.d(TAG, "settuping up media controller and views");
        Log.d(TAG, mPodcastAlarmLogoView.toString());
        if (mMediaController == null) {
            mMediaController = new MediaController(this);
            mMediaController.setAnchorView(mPodcastLogoWrapperView);
            mMediaController.setMediaPlayer(mService);
            mHandler.post(() -> {
                mMediaController.setEnabled(true);
                mMediaController.show();
            });
        }

        mPodcastAlarmLogoView.setOnClickListener((v) -> mMediaController.show());

        TextView podcastAlarmTitle = findViewById(R.id.alarm_text);
        podcastAlarmTitle.setText(alarm.getPodcast().getName());
    }

    private void stopPlaying() {
        if (mBound) {
            Log.d(TAG, "STOPPING");
            mMediaController.hide();
            mService.stop();
            mBound = false;
        }

        finish();

        if (isTaskRoot()) {
            startActivity(AlarmListActivity.getIntent(this));
        }
    }
}
