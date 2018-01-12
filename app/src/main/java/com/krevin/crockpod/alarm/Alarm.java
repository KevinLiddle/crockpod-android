package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.UUID;

public class Alarm {

    public static final String ALARM_ID_KEY = "alarm_id";
    public static final String ALARM_HOUR_KEY = "alarm_hour";
    public static final String ALARM_MINUTE_KEY = "alarm_minute";
    public static final String ALARM_ENABLED_KEY = "alarm_enabled";
    public static final String PODCAST_FEED_KEY = "podcast_feed";
    public static final String PODCAST_NAME_KEY = "podcast_name";
    public static final String PODCAST_AUTHOR_KEY = "podcast_author";
    public static final String PODCAST_LOGO_SMALL_KEY = "podcast_logo";
    public static final String PODCAST_LOGO_LARGE_KEY = "podcast_logo_large";

    private static final Duration REPEAT_DURATION = Duration.standardMinutes(1);
//    private static final Duration REPEAT_DURATION = Duration.standardDays(1);

    private final UUID mId;
    private final Context mContext;
    private final Podcast mPodcast;
    private final Integer mHourOfDay;
    private final Integer mMinute;
    private boolean mEnabled = true;

    public Alarm(Context context, Podcast podcast, int hourOfDay, int minute) {
        mId = UUID.randomUUID();
        mContext = context;
        mPodcast = podcast;
        mHourOfDay = hourOfDay;
        mMinute = minute;
    }

    public Alarm(Context context, Intent intent) {
        mContext = context;
        mId = UUID.fromString(intent.getStringExtra(ALARM_ID_KEY));
        mHourOfDay = intent.getIntExtra(ALARM_HOUR_KEY, 0);
        mMinute = intent.getIntExtra(ALARM_MINUTE_KEY, 0);
        mPodcast = buildPodcast(intent);
        mEnabled = intent.getBooleanExtra(ALARM_ENABLED_KEY, false);
    }

    public UUID getId() {
        return mId;
    }

    public Context getContext() {
        return mContext;
    }

    public Intent getIntent() {
        Intent intent = AlarmReceiver.getIntent(mContext);
        intent.putExtra(ALARM_ID_KEY, mId.toString());
        intent.putExtra(ALARM_HOUR_KEY, mHourOfDay);
        intent.putExtra(ALARM_MINUTE_KEY, mMinute);
        intent.putExtra(ALARM_ENABLED_KEY, mEnabled);
        intent.putExtra(PODCAST_NAME_KEY, mPodcast.getName());
        intent.putExtra(PODCAST_FEED_KEY, mPodcast.getRssFeedUrl());
        intent.putExtra(PODCAST_AUTHOR_KEY, mPodcast.getAuthor());
        intent.putExtra(PODCAST_LOGO_SMALL_KEY, mPodcast.getLogoUrlSmall());
        intent.putExtra(PODCAST_LOGO_LARGE_KEY, mPodcast.getLogoUrlLarge());
        return intent;
    }

    public Podcast getPodcast() {
        return mPodcast;
    }

    public int getHourOfDay() {
        return mHourOfDay;
    }

    public int getMinute() {
        return mMinute;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void toggle(boolean enabled) {
        mEnabled = enabled;
    }

    public Alarm buildNextAlarm() {
        Intent intent = (Intent) getIntent().clone();
        intent.putExtra(ALARM_HOUR_KEY, getNextTriggerTime().getHourOfDay());
        intent.putExtra(ALARM_MINUTE_KEY, getNextTriggerTime().getMinuteOfHour());
        return new Alarm(getContext(), intent);
    }

    public DateTime getNextTriggerTime() {
        DateTime target = DateTime.now()
                .withHourOfDay(getHourOfDay())
                .withMinuteOfHour(getMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return target.isBeforeNow() ? target.plus(REPEAT_DURATION) : target;
    }

    @Override
    public String toString() {
        return "Alarm{id=" + getId() +
                ",hourOfDay=" + getHourOfDay() +
                ",minuteOfHour=" + getMinute() +
                ",podcast=" + getPodcast().toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Alarm alarm = (Alarm) o;

        if (getPodcast() != null ? !getPodcast().equals(alarm.getPodcast()) : alarm.getPodcast() != null)
            return false;
        if (getHourOfDay() != alarm.getHourOfDay())
            return false;
        return getMinute() == alarm.getMinute();
    }



    private Podcast buildPodcast(Intent intent) {
        return new Podcast(
                intent.getStringExtra(PODCAST_NAME_KEY),
                intent.getStringExtra(PODCAST_FEED_KEY),
                intent.getStringExtra(PODCAST_AUTHOR_KEY),
                intent.getStringExtra(PODCAST_LOGO_SMALL_KEY),
                intent.getStringExtra(PODCAST_LOGO_LARGE_KEY)
        );
    }
}
