package com.krevin.crockpod.alarm.repositories;

import android.app.Activity;
import android.content.Context;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmDataRepositoryTest {

    private Context context;
    private AlarmDataRepository alarmDataRepository;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.buildActivity(Activity.class).get();
        alarmDataRepository = new AlarmDataRepository(context);
    }

    @Test
    public void canPutAndGetAlarms() {
        Podcast podcast = new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art");
        Alarm alarm = new Alarm(context, podcast, 14, 33);

        Integer id = alarmDataRepository.put(alarm);
        Alarm fetchedAlarm = alarmDataRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    @Test
    public void putSetsIdOnAlarmIfNull() {
        Podcast podcast = new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art");
        Alarm alarm = new Alarm(context, podcast, 18, 7);

        Integer id = alarmDataRepository.put(alarm);
        Alarm fetchedAlarm = alarmDataRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    @Test
    public void allReturnsAllAddedAlarms() {
        Alarm alarm1 = new Alarm(context, new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art"), 9, 25);
        Alarm alarm2 = new Alarm(context, new Podcast("neat_podcast", "neat_podcast_url", "neat_author", "neat_art"), 8, 37);
        Alarm alarm3 = new Alarm(context, new Podcast("lame_podcast", "lame_podcast_url", "lame_author", "lame_art"), 20, 50);

        alarmDataRepository.put(alarm1);
        alarmDataRepository.put(alarm2);
        alarmDataRepository.put(alarm3);

        List<Alarm> alarms = alarmDataRepository.all();

        assertAlarmsEqual(alarm1, alarms.get(0));
        assertAlarmsEqual(alarm2, alarms.get(1));
        assertAlarmsEqual(alarm3, alarms.get(2));
    }

    @Test
    public void removeRemovesAlarmsFromRepoById() {
        Alarm alarm1 = new Alarm(context, new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art"), 4, 40);
        Alarm alarm2 = new Alarm(context, new Podcast("neat_podcast", "neat_podcast_url", "neat_author", "neat_art"), 3, 30);
        Alarm alarm3 = new Alarm(context, new Podcast("lame_podcast", "lame_podcast_url", "lame_author", "lame_art"), 2, 20);

        alarmDataRepository.put(alarm1);
        int id2 = alarmDataRepository.put(alarm2);
        alarmDataRepository.put(alarm3);

        List<Alarm> alarms = alarmDataRepository.all();

        assertAlarmsEqual(alarm1, alarms.get(0));
        assertAlarmsEqual(alarm2, alarms.get(1));
        assertAlarmsEqual(alarm3, alarms.get(2));

        alarmDataRepository.remove(id2);

        List<Alarm> currentAlarms = alarmDataRepository.all();

        assertEquals(2, currentAlarms.size());
        assertAlarmsEqual(alarm1, currentAlarms.get(0));
        assertAlarmsEqual(alarm3, currentAlarms.get(1));
    }

    @Test
    public void putReplacesAnAlarmById() {
        Podcast podcast = new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art");
        Alarm alarm = new Alarm(context, podcast, 14, 33);

        Integer id = alarmDataRepository.put(alarm);

        alarm.setHourOfDay(15);
        alarm.setMinute(44);
        alarmDataRepository.put(alarm);

        Alarm fetchedAlarm = alarmDataRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertEquals(1, alarmDataRepository.all().size());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    private void assertAlarmsEqual(Alarm expected, Alarm actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getIntent().getComponent(), actual.getIntent().getComponent());
        assertEquals(expected.getPodcast().getRssFeedUrl(), actual.getPodcast().getRssFeedUrl());
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getMinute(), actual.getMinute());
    }
}
