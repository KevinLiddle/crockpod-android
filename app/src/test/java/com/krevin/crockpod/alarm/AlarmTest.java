package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmTest {

    private Context context;
    private Podcast podcast;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        podcast = new Podcast("name", "feed", "author", "logo", "logo_big");
    }

    @Test
    public void creatingAlarmWithIntentGetsDataFromIntent() {
        UUID alarmId = UUID.randomUUID();
        Intent intent = AlarmReceiver.getIntent(context);
        intent.putExtra(Alarm.PODCAST_FEED_KEY, podcast.getRssFeedUrl());
        intent.putExtra(Alarm.PODCAST_NAME_KEY, podcast.getName());
        intent.putExtra(Alarm.PODCAST_AUTHOR_KEY, podcast.getAuthor());
        intent.putExtra(Alarm.PODCAST_LOGO_SMALL_KEY, podcast.getLogoUrlSmall());
        intent.putExtra(Alarm.PODCAST_LOGO_LARGE_KEY, podcast.getLogoUrlLarge());
        intent.putExtra(Alarm.ALARM_HOUR_KEY, 12);
        intent.putExtra(Alarm.ALARM_MINUTE_KEY, 34);
        intent.putExtra(Alarm.ALARM_ID_KEY, alarmId.toString());
        intent.putExtra(Alarm.ALARM_ENABLED_KEY, false);
        Alarm alarm = new Alarm(context, intent);

        assertEquals(podcast.getRssFeedUrl(), alarm.getPodcast().getRssFeedUrl());
        assertEquals(podcast.getName(), alarm.getPodcast().getName());
        assertEquals(podcast.getAuthor(), alarm.getPodcast().getAuthor());
        assertEquals(podcast.getLogoUrlSmall(), alarm.getPodcast().getLogoUrlSmall());
        assertEquals(podcast.getLogoUrlLarge(), alarm.getPodcast().getLogoUrlLarge());
        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertEquals(alarmId, alarm.getId());
        assertFalse(alarm.isEnabled());
        assertTrue(intent.filterEquals(alarm.getIntent()));
        assertEquals(intent.getExtras().toString(), alarm.getIntent().getExtras().toString());
    }

    @Test
    public void creatingAlarmWithPodcastGetsDataFromPodcast() {
        Alarm alarm = new Alarm(context, podcast, 12, 34);

        assertEquals(podcast.getRssFeedUrl(), alarm.getPodcast().getRssFeedUrl());
        assertEquals(podcast.getName(), alarm.getPodcast().getName());
        assertEquals(podcast.getAuthor(), alarm.getPodcast().getAuthor());
        assertEquals(podcast.getLogoUrlSmall(), alarm.getPodcast().getLogoUrlSmall());
        assertEquals(podcast.getLogoUrlLarge(), alarm.getPodcast().getLogoUrlLarge());
        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertNotNull(alarm.getId());
        assertTrue(alarm.isEnabled());

        Intent intent = AlarmReceiver.getIntent(context);
        intent.putExtra(Alarm.PODCAST_FEED_KEY, "feed");
        intent.putExtra(Alarm.PODCAST_NAME_KEY, "name");
        intent.putExtra(Alarm.PODCAST_AUTHOR_KEY, "author");
        intent.putExtra(Alarm.PODCAST_LOGO_SMALL_KEY, "logo");
        intent.putExtra(Alarm.PODCAST_LOGO_LARGE_KEY, "logo_big");
        intent.putExtra(Alarm.ALARM_ID_KEY, alarm.getId());
        intent.putExtra(Alarm.ALARM_HOUR_KEY, 12);
        intent.putExtra(Alarm.ALARM_MINUTE_KEY, 34);
        intent.putExtra(Alarm.ALARM_ENABLED_KEY, true);

        assertTrue(intent.filterEquals(alarm.getIntent()));
        assertEquals(intent.getExtras().toString(), alarm.getIntent().getExtras().toString());

        alarm.toggle(false);
        intent.putExtra(Alarm.ALARM_ENABLED_KEY, false);
        assertTrue(intent.filterEquals(alarm.getIntent()));
    }

    @Test
    public void getNextTriggerTimeReturnsTheNextDateTimeOfTheHourAndMinute() {
        DateTime now = DateTime.now();
        int hour = (now.getHourOfDay() + 1) % 24;
        int minute = (now.getMinuteOfHour() + 5) % 60;

        Alarm alarm = new Alarm(context, podcast, hour, minute);

        DateTime triggerTime = alarm.getNextTriggerTime();

        assertEquals(hour, triggerTime.getHourOfDay());
        assertEquals(minute, triggerTime.getMinuteOfHour());
        assertEquals(0, triggerTime.getSecondOfMinute());
        assertEquals(0, triggerTime.getMillisOfSecond());
        assertTrue(triggerTime.isAfter(now));
    }

    @Test
    public void getNextTriggerTimeReturnsTheNextDateTimeOfTheHourAndMinuteWhenThe() {
        DateTime now = DateTime.now();
        DateTime before = new DateTime(now).minusHours(1).minusMinutes(5);

        Alarm alarm = new Alarm(context, podcast, before.getHourOfDay(), before.getMinuteOfHour());

        DateTime triggerTime = alarm.getNextTriggerTime();

        assertEquals(before.getHourOfDay(), triggerTime.getHourOfDay());
        assertEquals(before.getMinuteOfHour(), triggerTime.getMinuteOfHour());
        assertTrue(triggerTime.isAfter(now));
    }
}
