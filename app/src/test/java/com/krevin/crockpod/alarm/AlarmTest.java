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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void creatingAlarmWithIntentGetsDataFromIntent() {
        Intent intent = new Intent();
        intent.putExtra(Alarm.PODCAST_FEED_KEY, "feed");
        intent.putExtra(Alarm.PODCAST_NAME_KEY, "name");
        intent.putExtra(Alarm.PODCAST_AUTHOR_KEY, "author");
        intent.putExtra(Alarm.PODCAST_LOGO_KEY, "logo");
        intent.putExtra(Alarm.ALARM_HOUR_KEY, 12);
        intent.putExtra(Alarm.ALARM_MINUTE_KEY, 34);
        Alarm alarm = new Alarm(context, intent);

        assertEquals("feed", alarm.getPodcast().getRssFeedUrl());
        assertEquals("name", alarm.getPodcast().getName());
        assertEquals("author", alarm.getPodcast().getAuthor());
        assertEquals("logo", alarm.getPodcast().getLogoUrl());
        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertEquals(alarm.hashCode(), alarm.getId());
        assertEquals(intent, alarm.getIntent());
    }

    @Test
    public void creatingAlarmWithPodcastGetsDataFromPodcast() {
        Podcast podcast = new Podcast("name", "feed", "author", "logo");
        Alarm alarm = new Alarm(context, podcast, 12, 34);

        assertEquals("feed", alarm.getPodcast().getRssFeedUrl());
        assertEquals("name", alarm.getPodcast().getName());
        assertEquals("author", alarm.getPodcast().getAuthor());
        assertEquals("logo", alarm.getPodcast().getLogoUrl());
        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertEquals(alarm.hashCode(), alarm.getId());

        Intent intent = AlarmReceiver.getIntent(context);
        intent.putExtra(Alarm.PODCAST_FEED_KEY, "feed");
        intent.putExtra(Alarm.PODCAST_NAME_KEY, "name");
        intent.putExtra(Alarm.PODCAST_AUTHOR_KEY, "author");
        intent.putExtra(Alarm.PODCAST_LOGO_KEY, "logo");
        intent.putExtra(Alarm.ALARM_HOUR_KEY, 12);
        intent.putExtra(Alarm.ALARM_MINUTE_KEY, 34);

        assertTrue(intent.filterEquals(alarm.getIntent()));
    }

    @Test
    public void canSetHourOfDayAndMinute() {
        Podcast podcast = new Podcast("", "", "", "");
        Alarm alarm = new Alarm(context, podcast, 12, 34);

        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertEquals(12, alarm.getIntent().getIntExtra(Alarm.ALARM_HOUR_KEY, 0));
        assertEquals(34, alarm.getIntent().getIntExtra(Alarm.ALARM_MINUTE_KEY, 0));

        alarm.setHourOfDay(13);
        alarm.setMinute(45);

        assertEquals(13, alarm.getHourOfDay());
        assertEquals(45, alarm.getMinute());
        assertEquals(13, alarm.getIntent().getIntExtra(Alarm.ALARM_HOUR_KEY, 0));
        assertEquals(45, alarm.getIntent().getIntExtra(Alarm.ALARM_MINUTE_KEY, 0));
    }

    @Test
    public void getNextTriggerTimeReturnsTheNextDateTimeOfTheHourAndMinute() {
        DateTime now = DateTime.now();
        int hour = (now.getHourOfDay() + 1) % 24;
        int minute = (now.getMinuteOfHour() + 5) % 60;

        Alarm alarm = new Alarm(context, null, hour, minute);

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

        Alarm alarm = new Alarm(context, null, before.getHourOfDay(), before.getMinuteOfHour());

        DateTime triggerTime = alarm.getNextTriggerTime();

        assertEquals(before.getHourOfDay(), triggerTime.getHourOfDay());
        assertEquals(before.getMinuteOfHour(), triggerTime.getMinuteOfHour());
        assertTrue(triggerTime.isAfter(now));
    }

}
