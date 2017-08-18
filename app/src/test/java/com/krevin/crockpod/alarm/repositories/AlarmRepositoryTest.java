package com.krevin.crockpod.alarm.repositories;

import android.content.Intent;

import com.krevin.crockpod.alarm.Alarm;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlarmRepositoryTest {

    private AlarmDataRepository dataRepo;
    private AlarmServiceRepository serviceRepo;
    private AlarmRepository repo;

    @Before
    public void setUp() {
        dataRepo = mock(AlarmDataRepository.class);
        serviceRepo = mock(AlarmServiceRepository.class);
        repo = new AlarmRepository(dataRepo, serviceRepo);
    }

    @Test
    public void setSetsTheAlarmHourAndMinuteBeforeAddingItToTheOtherRepositories() {
        int hourOfDay = 12;
        int minute = 32;
        DateTime triggerTime = DateTime.now()
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute);

        Alarm dataRepoAlarm = new Alarm(null, mock(Intent.class));
        Alarm serviceRepoAlarm = new Alarm(null, mock(Intent.class));
        doAnswer(args -> {
            Alarm a = args.getArgument(0);
            dataRepoAlarm.setHourOfDay(a.getHourOfDay());
            dataRepoAlarm.setMinute(a.getMinute());
            return null;
        }).when(dataRepo).add(any());
        doAnswer(args -> {
            Alarm a = args.getArgument(0);
            serviceRepoAlarm.setHourOfDay(a.getHourOfDay());
            serviceRepoAlarm.setMinute(a.getMinute());
            return null;
        }).when(serviceRepo).set(any());

        Alarm alarm = spy(new Alarm(null, mock(Intent.class)));
        doReturn(triggerTime).when(alarm).getNextTriggerTime();

        repo.set(alarm);

        assertEquals(hourOfDay, dataRepoAlarm.getHourOfDay());
        assertEquals(minute, dataRepoAlarm.getMinute());
        assertEquals(hourOfDay, serviceRepoAlarm.getHourOfDay());
        assertEquals(minute, serviceRepoAlarm.getMinute());
        assertEquals(alarm.getHourOfDay(), hourOfDay);
        assertEquals(alarm.getMinute(), minute);
    }

    @Test
    public void cancelDelegatesToTheRepositories() {
        Alarm alarm = mock(Alarm.class);
        when(alarm.getId()).thenReturn(33);

        repo.cancel(alarm);

        verify(serviceRepo).cancel(alarm);
        verify(dataRepo).remove(alarm);
    }

    @Test
    public void listDelegatesToTheDataRepository() {
        List<Alarm> expected = new ArrayList<Alarm>() {{
            add(mock(Alarm.class));
        }};
        when(dataRepo.all()).thenReturn(expected);

        List<Alarm> actual = repo.list();

        assertEquals(expected, actual);
    }
}
