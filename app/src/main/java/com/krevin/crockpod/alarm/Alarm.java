package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

public class Alarm {

    public static final String PODCAST_NAME_KEY = "podcast_name";
    public static final String PODCAST_FEED_KEY = "podcast_feed";
    private static final String ALARM_TIME_KEY = "alarm_time";

    private int mId;
    private Intent mIntent;

    public Alarm(int id, Intent intent) {
        mId = id;
        mIntent = intent;
    }

    public Alarm(String podcastName, String podcastUrl, Calendar time, Context context) {
        mIntent = AlarmReceiver.getIntent(context);
        mIntent.putExtra(PODCAST_NAME_KEY, podcastName);
        mIntent.putExtra(PODCAST_FEED_KEY, podcastUrl);
        mIntent.putExtra(ALARM_TIME_KEY, time.getTimeInMillis());
    }

    public int getId() {
        return mId;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public String getPodcastName() {
        return mIntent.getStringExtra(PODCAST_NAME_KEY);
    }

    public String getPodcastUrl() {
        return mIntent.getStringExtra(PODCAST_FEED_KEY);
    }

    public Date getTime() {
        long timeInMillis = mIntent.getLongExtra(ALARM_TIME_KEY, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return calendar.getTime();
    }
}
