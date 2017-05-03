package com.krevin.crockpod.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TimePicker;

import com.krevin.crockpod.AutoCompleteSearchView;
import com.krevin.crockpod.R;
import com.krevin.crockpod.podcast.Podcast;
import com.krevin.crockpod.podcast.PodcastSearch;

import java.util.Calendar;

public class SetAlarmActivity extends Activity {

    private AlarmRepository mAlarmRepository;
    private Podcast mPodcast;

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

        final AutoCompleteSearchView<Podcast> podcastSearchField = (AutoCompleteSearchView<Podcast>) findViewById(R.id.podcast_rss_feed);
        podcastSearchField.init(R.layout.podcast_search_item, new PodcastSearch(this)::search);
        podcastSearchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                timePicker.setVisibility(View.GONE);
            } else {
                timePicker.setVisibility(View.VISIBLE);
                closeKeyboard(v);
            }
        });
        podcastSearchField.setOnItemClickListener((parent, view, position, id) -> {
            Podcast podcast = (Podcast) parent.getItemAtPosition(position);
            mPodcast = podcast;
            podcastSearchField.setText(podcast.getName());
            podcastSearchField.clearFocus();
        });

        Button setAlarmButton = (Button) findViewById(R.id.set_alarm);
        setAlarmButton.setOnClickListener(v -> {
            if (mPodcast != null) {
                createAlarm(
                        timePicker.getHour(),
                        timePicker.getMinute(),
                        mPodcast
                );
                finish();
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void createAlarm(int hour, int minute, Podcast podcast) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Alarm alarm = new Alarm(podcast.getName(), podcast.getRssFeedUrl(), calendar, this);

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
