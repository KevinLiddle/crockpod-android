package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;

public class Alarm {

    static final String PODCAST_FEED_KEY = "podcast_feed";
    static final String PODCAST_NAME_KEY = "podcast_name";
    static final String PODCAST_AUTHOR_KEY = "podcast_author";
    static final String PODCAST_LOGO_KEY = "podcast_logo";
    static final String ALARM_ID_KEY = "alarm_id";
    static final String ALARM_HOUR_KEY = "alarm_hour";
    static final String ALARM_MINUTE_KEY = "alarm_minute";

    private Intent mIntent;
    private Podcast mPodcast;
    private Integer mHourOfDay;
    private Integer mMinute;
    private Context mContext;
    private Integer mId;

    public Alarm(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    public Alarm(Context context, Podcast podcast, int hourOfDay, int minute) {
        mContext = context;
        mPodcast = podcast;
        mHourOfDay = hourOfDay;
        mMinute = minute;
    }

    public static boolean exists(Intent intent) {
        return intent.hasExtra(PODCAST_FEED_KEY);
    }

    public Integer getId() {
        if (mId != null) {
            return mId;
        }
        if (mIntent != null && mIntent.hasExtra(ALARM_ID_KEY)) {
            return mIntent.getIntExtra(ALARM_ID_KEY, 0);
        }
        return null;
    }

    public void setId(int id) {
        mId = id;
    }

    public Intent getIntent() {
        return mIntent == null ? buildIntent() : mIntent;
    }

    public Podcast getPodcast() {
        return mPodcast == null ? buildPodcast() : mPodcast;
    }

    public int getHourOfDay() {
        return mHourOfDay == null ? mIntent.getIntExtra(ALARM_HOUR_KEY, 0) : mHourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        mHourOfDay = hourOfDay;
        getIntent().putExtra(ALARM_HOUR_KEY, hourOfDay);
    }

    public int getMinute() {
        return mMinute == null ? mIntent.getIntExtra(ALARM_MINUTE_KEY, 0) : mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
        getIntent().putExtra(ALARM_MINUTE_KEY, minute);
    }

    public DateTime getNextTriggerTime() {
        DateTime now = DateTime.now();
        DateTime target = now
                .withHourOfDay(getHourOfDay())
                .withMinuteOfHour(getMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return target.isAfter(now) ? target : target.plusDays(1); // target.plusMinutes(2);
    }

    private Podcast buildPodcast() {
        return new Podcast(
                mIntent.getStringExtra(PODCAST_NAME_KEY),
                mIntent.getStringExtra(PODCAST_FEED_KEY),
                mIntent.getStringExtra(PODCAST_AUTHOR_KEY),
                mIntent.getStringExtra(PODCAST_LOGO_KEY));
    }

    private Intent buildIntent() {
        Intent intent = AlarmReceiver.getIntent(mContext);
        intent.putExtra(ALARM_ID_KEY, mId);
        intent.putExtra(ALARM_HOUR_KEY, mHourOfDay);
        intent.putExtra(ALARM_MINUTE_KEY, mMinute);
        intent.putExtra(PODCAST_NAME_KEY, mPodcast.getName());
        intent.putExtra(PODCAST_FEED_KEY, mPodcast.getRssFeedUrl());
        intent.putExtra(PODCAST_AUTHOR_KEY, mPodcast.getAuthor());
        intent.putExtra(PODCAST_LOGO_KEY, mPodcast.getLogoUrl());
        return intent;
    }
}
