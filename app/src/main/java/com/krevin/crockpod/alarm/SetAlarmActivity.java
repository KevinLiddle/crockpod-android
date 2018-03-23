package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.NetworkImageView;
import com.krevin.crockpod.AutoCompleteSearchView;
import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.HttpClient;
import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.krevin.crockpod.podcast.Podcast;
import com.krevin.crockpod.podcast.PodcastSearch;

import java.util.Calendar;

public class SetAlarmActivity extends CrockpodActivity {

    private Alarm mAlarm;
    private AlarmRepository mAlarmRepository;
    private AutoCompleteSearchView<Podcast> mPodcastSearchField;
    private TimePicker mTimePicker;
    private View mSelectedPodcast;
    private HttpClient mHttpClient;

    public static Intent getIntent(Context context) {
        return new Intent(context, SetAlarmActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getIntent(Context context, Alarm alarm) {
        return getIntent(context).putExtra(Alarm.ALARM_ID_KEY, alarm.getId().toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        mAlarmRepository = new AlarmRepository(this);

        String alarmId = getIntent().getStringExtra(Alarm.ALARM_ID_KEY);
        Calendar calendar = Calendar.getInstance();
        mAlarm = alarmId == null
                ? new Alarm(this, null, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                : mAlarmRepository.find(alarmId);

        mPodcastSearchField = findViewById(R.id.podcast_rss_feed);
        mTimePicker = findViewById(R.id.time_picker);
        mSelectedPodcast = findViewById(R.id.selected_podcast);
        mHttpClient = getCrockpodApp().getHttpClient();

        mTimePicker.setHour(mAlarm.getHourOfDay());
        mTimePicker.setMinute(mAlarm.getMinute());

        PodcastArrayAdapter podcastArrayAdapter = new PodcastArrayAdapter(this, mHttpClient.getImageLoader());
        PodcastSearch podcastSearch = new PodcastSearch(mHttpClient);

        mPodcastSearchField.init(podcastArrayAdapter, podcastSearch::search);
        mPodcastSearchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mTimePicker.setVisibility(View.GONE);
            } else {
                mTimePicker.setVisibility(View.VISIBLE);
                closeKeyboard(v);
            }
            refreshViews();
        });

        mPodcastSearchField.setOnItemClickListener((parent, view, position, id) -> {
            mAlarm.setPodcast((Podcast) parent.getItemAtPosition(position));
            mPodcastSearchField.clearFocus();
        });

        mSelectedPodcast.findViewById(R.id.unselect_podcast_button).setOnClickListener(v -> {
            mAlarm.setPodcast(null);
            refreshViews();
        });

        Button setAlarmButton = findViewById(R.id.set_alarm);
        setAlarmButton.setOnClickListener(v -> {
            if (mAlarm.getPodcast() != null) {
                mAlarm.setHourOfDay(mTimePicker.getHour());
                mAlarm.setMinute(mTimePicker.getMinute());
                mAlarmRepository.set(mAlarm);
                finish();
            }
        });

        refreshViews();
    }

    private void refreshViews() {
        if (mAlarm.getPodcast() == null) {
            mSelectedPodcast.setVisibility(View.GONE);
            mPodcastSearchField.setVisibility(View.VISIBLE);
        } else {
            mSelectedPodcast.setVisibility(View.VISIBLE);
            mPodcastSearchField.setVisibility(View.GONE);
            mPodcastSearchField.setText("");

            TextView podcastTitle = mSelectedPodcast.findViewById(R.id.selected_podcast_title);
            NetworkImageView podcastLogo = findViewById(R.id.set_alarm_podcast_logo);
            podcastTitle.setText(mAlarm.getPodcast().getName());
            podcastLogo.setImageUrl(mAlarm.getPodcast().getLogoUrlSmall(), mHttpClient.getImageLoader());
        }
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
