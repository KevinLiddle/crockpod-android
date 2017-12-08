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

import com.krevin.crockpod.AutoCompleteSearchView;
import com.krevin.crockpod.CrockpodActivity;
import com.krevin.crockpod.HttpClient;
import com.krevin.crockpod.R;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.krevin.crockpod.podcast.Podcast;
import com.krevin.crockpod.podcast.PodcastSearch;

public class SetAlarmActivity extends CrockpodActivity {

    private Podcast mPodcast;

    public static Intent getIntent(Context context) {
        return new Intent(context, SetAlarmActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        final TimePicker timePicker = findViewById(R.id.time_picker);
        final AutoCompleteSearchView<Podcast> podcastSearchField = findViewById(R.id.podcast_rss_feed);
        final View selectedPodcast = findViewById(R.id.selected_podcast);

        HttpClient httpClient = getCrockpodApp().getHttpClient();
        PodcastArrayAdapter podcastArrayAdapter = new PodcastArrayAdapter(this, httpClient.getImageLoader());
        PodcastSearch podcastSearch = new PodcastSearch(httpClient);

        podcastSearchField.init(podcastArrayAdapter, podcastSearch::search);
        podcastSearchField.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                timePicker.setVisibility(View.GONE);
            } else {
                timePicker.setVisibility(View.VISIBLE);
                if (mPodcast != null) {
                    podcastSearchField.setText("");
                    podcastSearchField.setVisibility(View.GONE);
                    selectedPodcast.setVisibility(View.VISIBLE);
                    TextView podcastTitle = selectedPodcast.findViewById(R.id.selected_podcast_title);
                    podcastTitle.setText(mPodcast.getName());
                }
                closeKeyboard(v);
            }
        });

        podcastSearchField.setOnItemClickListener((parent, view, position, id) -> {
            mPodcast = (Podcast) parent.getItemAtPosition(position);
            podcastSearchField.clearFocus();
        });

        selectedPodcast.findViewById(R.id.unselect_podcast_button).setOnClickListener(v -> {
            mPodcast = null;
            selectedPodcast.setVisibility(View.GONE);
            podcastSearchField.setVisibility(View.VISIBLE);
        });

        Button setAlarmButton = findViewById(R.id.set_alarm);
        setAlarmButton.setOnClickListener(v -> {
            if (mPodcast != null) {
                Alarm alarm = new Alarm(this, mPodcast, timePicker.getHour(), timePicker.getMinute());
                new AlarmRepository(this).set(alarm);
                finish();
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
