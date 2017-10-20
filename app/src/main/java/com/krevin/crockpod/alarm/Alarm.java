package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.UUID;

public class Alarm {

    static final String ALARM_ID_KEY = "alarm_id";
    static final String ALARM_HOUR_KEY = "alarm_hour";
    static final String ALARM_MINUTE_KEY = "alarm_minute";
    static final String PODCAST_FEED_KEY = "podcast_feed";
    static final String PODCAST_NAME_KEY = "podcast_name";
    static final String PODCAST_AUTHOR_KEY = "podcast_author";
    static final String PODCAST_LOGO_KEY = "podcast_logo";

//    private static final Duration REPEAT_DURATION = Duration.standardMinutes(1);
    private static final Duration REPEAT_DURATION = Duration.standardDays(1);

    private final UUID mId;
    private final Intent mIntent;
    private final Podcast mPodcast;
    private final Integer mHourOfDay;
    private final Integer mMinute;

    public Alarm(Context context, Podcast podcast, int hourOfDay, int minute) {
        mId = UUID.randomUUID();
        mIntent = buildIntent(mId, context, hourOfDay, minute, podcast);
        mPodcast = podcast;
        mHourOfDay = hourOfDay;
        mMinute = minute;
    }

    public Alarm(Intent intent) {
        mIntent = intent;
        mId = UUID.fromString(intent.getStringExtra(ALARM_ID_KEY));
        mHourOfDay = intent.getIntExtra(ALARM_HOUR_KEY, 0);
        mMinute = intent.getIntExtra(ALARM_MINUTE_KEY, 0);
        mPodcast = buildPodcast(intent);
    }

    public Alarm(Intent intent, int hour, int minute) {
        mIntent = intent;
        mId = UUID.fromString(intent.getStringExtra(ALARM_ID_KEY));
        mHourOfDay = hour;
        mMinute = minute;
        mPodcast = buildPodcast(intent);
    }

    public static Alarm buildNextAlarm(Intent intent) {
        Alarm currentAlarm = new Alarm(intent);
        intent.putExtra(ALARM_HOUR_KEY, currentAlarm.getNextTriggerTime().getHourOfDay());
        intent.putExtra(ALARM_MINUTE_KEY, currentAlarm.getNextTriggerTime().getMinuteOfHour());
        return new Alarm(intent);
    }

    public UUID getId() {
        return mId;
    }

    public Intent getIntent() {
        return mIntent;
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

    public DateTime getNextTriggerTime() {
        DateTime now = DateTime.now();
        DateTime target = now
                .withHourOfDay(getHourOfDay())
                .withMinuteOfHour(getMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return target.isBefore(now) ? target.plus(REPEAT_DURATION) : target;
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
                intent.getStringExtra(PODCAST_LOGO_KEY));
    }

    private Intent buildIntent(UUID id, Context context, int hourOfDay, int minute, Podcast podcast) {
        Intent intent = AlarmReceiver.getIntent(context);
        intent.putExtra(ALARM_ID_KEY, id.toString());
        intent.putExtra(ALARM_HOUR_KEY, hourOfDay);
        intent.putExtra(ALARM_MINUTE_KEY, minute);
        intent.putExtra(PODCAST_NAME_KEY, podcast.getName());
        intent.putExtra(PODCAST_FEED_KEY, podcast.getRssFeedUrl());
        intent.putExtra(PODCAST_AUTHOR_KEY, podcast.getAuthor());
        intent.putExtra(PODCAST_LOGO_KEY, podcast.getLogoUrl());
        return intent;
    }
}
