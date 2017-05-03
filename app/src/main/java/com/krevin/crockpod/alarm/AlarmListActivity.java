package com.krevin.crockpod.alarm;

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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.krevin.crockpod.R;
import com.krevin.crockpod.UniqueIntId;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlarmListActivity extends Activity implements Callback {

    public static final String TAG = AlarmListActivity.class.getCanonicalName();
    public static final String ALARM_RINGING_KEY = "alarm_ringing";

    private MediaPlayer mMediaPlayer;
    private RecyclerView mAlarmList;
    private AlarmRepository mAlarmRepository;

    public static Intent getIntent(Context context) {
        return new Intent(context, AlarmListActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mAlarmRepository = new AlarmRepository(this);

        FloatingActionButton mAddAlarmButton = (FloatingActionButton) findViewById(R.id.add_alarm_button);
        mAddAlarmButton.setOnClickListener(view -> startActivity(SetAlarmActivity.getIntent(AlarmListActivity.this)));

        mAlarmList = (RecyclerView) findViewById(R.id.alarm_list);
        mAlarmList.setHasFixedSize(true);
        mAlarmList.setLayoutManager(new LinearLayoutManager(this));

        setUpAlarmRingingViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAlarmList();
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

    private void refreshAlarmList() {
        mAlarmList.swapAdapter(new AlarmListAdapter(mAlarmRepository.all()), true);
    }

    private void setUpAlarmRingingViews() {
        final View alarmMessage = findViewById(R.id.alarm_message);
        Button cancelButton = (Button) findViewById(R.id.alarm_cancel);

        cancelButton.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            alarmMessage.setVisibility(View.INVISIBLE);
        });

        if (getIntent().getBooleanExtra(ALARM_RINGING_KEY, false)) {
            alarmMessage.setVisibility(View.VISIBLE);
            showAlarmNotification();

            String feedUrl = getIntent().getStringExtra(Alarm.PODCAST_FEED_KEY);
            PkRSS.with(this).load(feedUrl).callback(this).async();
        }
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

    private class AlarmListAdapter extends RecyclerView.Adapter<AlarmHolder> {
        private final List<Alarm> mAlarms;

        AlarmListAdapter(List<Alarm> mAlarms) {
            this.mAlarms = mAlarms;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(AlarmListActivity.this);
            View view = inflater.inflate(R.layout.alarm_list_item, parent, false);
            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            holder.bindAlarm(mAlarms.get(position));
        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }
    }

    private class AlarmHolder extends RecyclerView.ViewHolder {

        private final TextView mAlarmTextView;
        private final ImageButton mDeleteAlarmButton;

        AlarmHolder(View itemView) {
            super(itemView);
            mAlarmTextView = (TextView) itemView.findViewById(R.id.alarm_text);
            mDeleteAlarmButton = (ImageButton) itemView.findViewById(R.id.delete_alarm_button);
        }

        void bindAlarm(final Alarm alarm) {
            SimpleDateFormat format = new SimpleDateFormat("h:ma");
            String text = format.format(alarm.getTime()) + " - " + alarm.getPodcastName();
            mAlarmTextView.setText(text);

            final AlarmManager alarmManager = (AlarmManager) AlarmListActivity.this.getSystemService(Context.ALARM_SERVICE);
            mDeleteAlarmButton.setOnClickListener(view -> {
                PendingIntent alarmPendingIntent = SetAlarmActivity.buildPendingIntent(
                        AlarmListActivity.this,
                        alarm.getId(),
                        alarm.getIntent()
                );
                alarmManager.cancel(alarmPendingIntent);
                mAlarmRepository.remove(alarm.getId());
                refreshAlarmList();
            });
        }
    }
}
