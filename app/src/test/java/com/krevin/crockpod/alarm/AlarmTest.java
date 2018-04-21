package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.podcast.Podcast;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
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
        intent.putExtra(Alarm.ALARM_REPEAT_DAYS_KEY, "true,false,true,false,false,true,false");
        Alarm alarm = new Alarm(context, intent);

        assertEquals(podcast.getRssFeedUrl(), alarm.getPodcast().getRssFeedUrl());
        assertEquals(podcast.getName(), alarm.getPodcast().getName());
        assertEquals(podcast.getAuthor(), alarm.getPodcast().getAuthor());
        assertEquals(podcast.getLogoUrlSmall(), alarm.getPodcast().getLogoUrlSmall());
        assertEquals(podcast.getLogoUrlLarge(), alarm.getPodcast().getLogoUrlLarge());
        assertEquals(12, alarm.getHourOfDay());
        assertEquals(34, alarm.getMinute());
        assertEquals(Arrays.asList(true, false, true, false, false, true, false), alarm.getRepeatDays());
        assertEquals(alarmId, alarm.getId());
        assertFalse(alarm.isEnabled());
        assertIntentsEqual(intent, alarm.getIntent());
    }

    @Test
    public void repeatDaysDefaultsToEveryDay() {
        UUID alarmId = UUID.randomUUID();

        Intent intent = AlarmReceiver.getIntent(context);
        intent.putExtra(Alarm.ALARM_ID_KEY, alarmId.toString());
        Alarm alarm = new Alarm(context, intent);

        assertEquals(Arrays.asList(true, true, true, true, true, true, true), alarm.getRepeatDays());
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

        Intent expectedIntent = AlarmReceiver.getIntent(context);
        expectedIntent.putExtra(Alarm.PODCAST_FEED_KEY, "feed");
        expectedIntent.putExtra(Alarm.PODCAST_NAME_KEY, "name");
        expectedIntent.putExtra(Alarm.PODCAST_AUTHOR_KEY, "author");
        expectedIntent.putExtra(Alarm.PODCAST_LOGO_SMALL_KEY, "logo");
        expectedIntent.putExtra(Alarm.PODCAST_LOGO_LARGE_KEY, "logo_big");
        expectedIntent.putExtra(Alarm.ALARM_ID_KEY, alarm.getId().toString());
        expectedIntent.putExtra(Alarm.ALARM_HOUR_KEY, 12);
        expectedIntent.putExtra(Alarm.ALARM_MINUTE_KEY, 34);
        expectedIntent.putExtra(Alarm.ALARM_ENABLED_KEY, true);
        expectedIntent.putExtra(Alarm.ALARM_REPEAT_DAYS_KEY, "false,false,false,false,false,false,false");

        assertIntentsEqual(expectedIntent, alarm.getIntent());

        alarm.toggle(false);
        expectedIntent.putExtra(Alarm.ALARM_ENABLED_KEY, false);
        assertIntentsEqual(expectedIntent, alarm.getIntent());
    }

    @Test
    public void setRepeatDaysErrorsIfLengthIsNotSeven() {
        Alarm alarm = new Alarm(context, podcast, 12, 34);
        List<Boolean> repeatDays = Arrays.asList(true, false, true, false, true, true, true);
        alarm.setRepeatDays(repeatDays);

        assertEquals(repeatDays, alarm.getRepeatDays());

        exception.expect(RuntimeException.class);
        exception.expectMessage("setRepeatDays must be called with 7 values! Duh!");
        alarm.setRepeatDays(Arrays.asList(true, false));
    }

    @Test
    public void getNextTriggerTimeReturnsTheNextDateTimeOfTheHourAndMinute() {
        DateTime now = DateTime.now();
        DateTime later = now.plusHours(1).plusMinutes(5);

        Alarm alarm = new Alarm(context, podcast, later.getHourOfDay(), later.getMinuteOfHour());

        DateTime triggerTime = alarm.getNextTriggerTime();

        assertEquals(later.getHourOfDay(), triggerTime.getHourOfDay());
        assertEquals(later.getMinuteOfHour(), triggerTime.getMinuteOfHour());
        assertEquals(0, triggerTime.getSecondOfMinute());
        assertEquals(0, triggerTime.getMillisOfSecond());
        assertTrue(triggerTime.isAfter(now));
    }

    @Test
    public void getNextTriggerTimeReturnsATimeForTomorrowWhenHourAndMinuteHavePassedForToday() {
        DateTime before = DateTime.now()
                .minusHours(1)
                .minusMinutes(5)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        Alarm alarm = new Alarm(context, podcast, before.getHourOfDay(), before.getMinuteOfHour());

        DateTime triggerTime = alarm.getNextTriggerTime();

        assertEquals(before.plusDays(1), triggerTime);
    }

    @Test
    public void getNextTriggerTimeReturnsTimeForNextEnabledRepeatDay() {
        DateTime before = DateTime.now()
                .minusHours(1)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        Alarm alarm = new Alarm(context, podcast, before.getHourOfDay(), before.getMinuteOfHour());

        int dayOfWeekIndex = before.getDayOfWeek(); // 1-indexed, starts on Sunday
        List<Boolean> repeatDays = Arrays.asList(true, true, true, true, true, true, true);

        repeatDays.set(dayOfWeekIndex % 7, false); // no-repeat tomorrow
        alarm.setRepeatDays(repeatDays);
        assertEquals(before.plusDays(2), alarm.getNextTriggerTime());

        repeatDays.set((dayOfWeekIndex + 1) % 7, false); // no-repeat two days from now
        repeatDays.set((dayOfWeekIndex + 2) % 7, false); // no-repeat three days from now
        alarm.setRepeatDays(repeatDays);
        assertEquals(before.plusDays(4), alarm.getNextTriggerTime());
    }

    @Test
    public void getNextTriggerTimeReturnsNextOccurenceOfTimeIfThereAreNoRepeatDays() {
        DateTime before = DateTime.now()
                .minusHours(1)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        Alarm alarm = new Alarm(context, podcast, before.getHourOfDay(), before.getMinuteOfHour());

        alarm.setRepeatDays(Arrays.asList(false, false, false, false, false, false, false));
        assertEquals(before.plusDays(1), alarm.getNextTriggerTime());
    }

    @Test
    public void isRepeatingIsTrueIfHasARepeatDay() {
        Alarm alarm = new Alarm(context, podcast, 1, 1);

        alarm.setRepeatDays(Arrays.asList(false, false, false, true, false, false, false));
        assertTrue(alarm.isRepeating());

        alarm.setRepeatDays(Arrays.asList(false, false, false, false, false, false, false));
        assertFalse(alarm.isRepeating());
    }

    private void assertIntentsEqual(Intent expected, Intent actual) {
        assertTrue(expected.filterEquals(actual));
        assertArrayEquals(expected.getExtras().keySet().toArray(), actual.getExtras().keySet().toArray());
        assertEquals(expected.getExtras().toString(), actual.getExtras().toString());
    }
}
