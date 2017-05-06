package com.krevin.crockpod.alarm;

import android.app.Activity;
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

public class SetAlarmActivity extends Activity {

    private AlarmRepository mAlarmRepository;
    private Podcast mPodcast;

    public static Intent getIntent(Context context) {
        return new Intent(context, SetAlarmActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        mAlarmRepository = new AlarmRepository(this);

        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
        final AutoCompleteSearchView<Podcast> podcastSearchField = (AutoCompleteSearchView<Podcast>) findViewById(R.id.podcast_rss_feed);

        podcastSearchField.init(new PodcastArrayAdapter(this), new PodcastSearch(this)::search);
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
                Alarm alarm = new Alarm(this, mPodcast, timePicker.getHour(), timePicker.getMinute());
                mAlarmRepository.add(alarm);

                alarm.set();
                finish();
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
