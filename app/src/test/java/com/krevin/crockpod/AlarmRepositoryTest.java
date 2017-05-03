package com.krevin.crockpod;

import android.app.Activity;
import android.content.Context;

import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.alarm.AlarmRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
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
        Alarm alarm = new Alarm("cool_podcast_url", Calendar.getInstance(), context);

        int id = alarmRepository.add(alarm);
        Alarm fetchedAlarm = alarmRepository.get(id);

        assertEquals(id, fetchedAlarm.getId());
        assertAlarmsEqual(alarm, fetchedAlarm);
    }

    @Test
    public void canGetAllAlarms() {
        Alarm alarm1 = new Alarm("cool_podcast_url", Calendar.getInstance(), context);
        Alarm alarm2 = new Alarm("neat_podcast_url", Calendar.getInstance(), context);
        Alarm alarm3 = new Alarm("lame_podcast_url", Calendar.getInstance(), context);

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
        Alarm alarm1 = new Alarm("cool_podcast_url", Calendar.getInstance(), context);
        Alarm alarm2 = new Alarm("neat_podcast_url", Calendar.getInstance(), context);
        Alarm alarm3 = new Alarm("lame_podcast_url", Calendar.getInstance(), context);

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
        assertEquals(expected.getIntent().getComponent(), actual.getIntent().getComponent());
        assertEquals(expected.getPodcastUrl(), actual.getPodcastUrl());
        assertEquals(expected.getTime(), actual.getTime());
    }
}
