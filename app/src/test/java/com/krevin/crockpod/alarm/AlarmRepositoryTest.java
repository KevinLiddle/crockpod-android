package com.krevin.crockpod.alarm;

import android.app.Activity;
import android.content.Context;

import com.krevin.crockpod.BuildConfig;
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
public class AlarmRepositoryTest {

    private Context context;
    private AlarmRepository alarmRepository;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.buildActivity(Activity.class).get();
        alarmRepository = new AlarmRepository(context);
    }

    @Test
    public void canAddAndGetAlarms() {
        Podcast podcast = new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art");
        Alarm alarm = new Alarm(context, podcast, 14, 33);

        int id = alarmRepository.add(alarm);
        Alarm fetchedAlarm = alarmRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    @Test
    public void setsIdOnAddedAlarm() {
        Podcast podcast = new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art");
        Alarm alarm = new Alarm(context, podcast, 18, 7);

        int id = alarmRepository.add(alarm);
        Alarm fetchedAlarm = alarmRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    @Test
    public void canGetAllAlarms() {
        Alarm alarm1 = new Alarm(context, new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art"), 9, 25);
        Alarm alarm2 = new Alarm(context, new Podcast("neat_podcast", "neat_podcast_url", "neat_author", "neat_art"), 8, 37);
        Alarm alarm3 = new Alarm(context, new Podcast("lame_podcast", "lame_podcast_url", "lame_author", "lame_art"), 20, 50);

        alarmRepository.add(alarm1);
        alarmRepository.add(alarm2);
        alarmRepository.add(alarm3);

        List<Alarm> alarms = alarmRepository.all();

        assertAlarmsEqual(alarm1, alarms.get(0));
        assertAlarmsEqual(alarm2, alarms.get(1));
        assertAlarmsEqual(alarm3, alarms.get(2));
    }

    @Test
    public void canRemoveAlarms() {
        Alarm alarm1 = new Alarm(context, new Podcast("cool_podcast", "cool_podcast_url", "cool_author", "cool_art"), 4, 40);
        Alarm alarm2 = new Alarm(context, new Podcast("neat_podcast", "neat_podcast_url", "neat_author", "neat_art"), 3, 30);
        Alarm alarm3 = new Alarm(context, new Podcast("lame_podcast", "lame_podcast_url", "lame_author", "lame_art"), 2, 20);

        alarmRepository.add(alarm1);
        int id2 = alarmRepository.add(alarm2);
        alarmRepository.add(alarm3);

        List<Alarm> alarms = alarmRepository.all();

        assertAlarmsEqual(alarm1, alarms.get(0));
        assertAlarmsEqual(alarm2, alarms.get(1));
        assertAlarmsEqual(alarm3, alarms.get(2));

        alarmRepository.remove(id2);

        List<Alarm> currentAlarms = alarmRepository.all();

        assertEquals(2, currentAlarms.size());
        assertAlarmsEqual(alarm1, currentAlarms.get(0));
        assertAlarmsEqual(alarm3, currentAlarms.get(1));
    }

    private void assertAlarmsEqual(Alarm expected, Alarm actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getIntent().getComponent(), actual.getIntent().getComponent());
        assertEquals(expected.getPodcast().getRssFeedUrl(), actual.getPodcast().getRssFeedUrl());
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getMinute(), actual.getMinute());
    }
}
