package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Alarm {

    public static final String ALARM_ID_KEY = "alarm_id";
    public static final String ALARM_HOUR_KEY = "alarm_hour";
    public static final String ALARM_MINUTE_KEY = "alarm_minute";
    public static final String ALARM_REPEAT_DAYS_KEY = "alarm_repeat_days";
    public static final String ALARM_ENABLED_KEY = "alarm_enabled";
    public static final String PODCAST_FEED_KEY = "podcast_feed";
    public static final String PODCAST_NAME_KEY = "podcast_name";
    public static final String PODCAST_AUTHOR_KEY = "podcast_author";
    public static final String PODCAST_LOGO_SMALL_KEY = "podcast_logo";
    public static final String PODCAST_LOGO_LARGE_KEY = "podcast_logo_large";

    private final UUID mId;
    private final Context mContext;
    private Podcast mPodcast;
    private int mHourOfDay;
    private int mMinute;
    private List<Boolean> mRepeatDays;
    private boolean mEnabled = true;

    public Alarm(Context context, Podcast podcast, int hourOfDay, int minute) {
        mId = UUID.randomUUID();
        mContext = context;
        mPodcast = podcast;
        mHourOfDay = hourOfDay;
        mMinute = minute;
        mRepeatDays = convertToList(null);
    }

    public Alarm(Context context, Intent intent) {
        mContext = context;
        mId = UUID.fromString(intent.getStringExtra(ALARM_ID_KEY));
        mHourOfDay = intent.getIntExtra(ALARM_HOUR_KEY, 0);
        mMinute = intent.getIntExtra(ALARM_MINUTE_KEY, 0);
        mRepeatDays = convertToList(intent.getStringExtra(ALARM_REPEAT_DAYS_KEY));
        mEnabled = intent.getBooleanExtra(ALARM_ENABLED_KEY, false);
        mPodcast = buildPodcast(intent);
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
        intent.putExtra(ALARM_REPEAT_DAYS_KEY, convertToString(mRepeatDays));
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

    public void setPodcast(Podcast podcast) {
        mPodcast = podcast;
    }

    public int getHourOfDay() {
        return mHourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        mHourOfDay = hourOfDay;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public List<Boolean> getRepeatDays() {
        return mRepeatDays;
    }

    public void setRepeatDays(List<Boolean> repeatDays) {
        if (repeatDays == null || repeatDays.size() != 7) {
            throw new RuntimeException("setRepeatDays must be called with 7 values! Duh!");
        }

        this.mRepeatDays = repeatDays;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void toggle(boolean enabled) {
        mEnabled = enabled;
    }

    public DateTime getNextTriggerTime() {
        DateTime target = DateTime.now()
                .withHourOfDay(getHourOfDay())
                .withMinuteOfHour(getMinute())
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return getNextRepeatTriggerTime(target);
    }

    @Override
    public String toString() {
        return "Alarm{id=" + getId() +
                ",hourOfDay=" + getHourOfDay() +
                ",minuteOfHour=" + getMinute() +
                ",repeatDays=" + getRepeatDays().toString() +
                ",enabled=" + isEnabled() +
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

        if (isEnabled() != alarm.isEnabled())
            return false;
        if (!getId().equals(alarm.getId()))
            return false;
        if (!getPodcast().equals(alarm.getPodcast()))
            return false;
        if (getHourOfDay() != alarm.getHourOfDay())
            return false;
        if (getMinute() != alarm.getMinute())
            return false;
        return getRepeatDays().equals(alarm.getRepeatDays());
    }

    public boolean isRepeating() {
        return getRepeatDays().stream().anyMatch(d -> d);
    }

    private DateTime getNextRepeatTriggerTime(DateTime target) {
        boolean activeOnDay = getRepeatDays().get(target.getDayOfWeek() - 1);

        if (target.isAfterNow() && (activeOnDay || !isRepeating())) {
            return target;
        }
        return getNextRepeatTriggerTime(target.plusDays(1));
    }

    private String convertToString(List<Boolean> repeatDays) {
        return repeatDays.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private List<Boolean> convertToList(String repeatDays) {
        if (repeatDays == null) {
            return Stream.generate(() -> true).limit(7).collect(Collectors.toList());
        }

        return Arrays.stream(repeatDays.split(","))
                .map(Boolean::valueOf)
                .collect(Collectors.toList());
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
