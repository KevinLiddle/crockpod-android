package com.krevin.crockpod.alarm.repositories;

import com.krevin.crockpod.alarm.Alarm;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
    public void setAddsTheAlarmToTheOtherRepositories() {
        Alarm alarm = mock(Alarm.class);

        repo.set(alarm);

        verify(dataRepo).add(alarm);
        verify(serviceRepo).set(alarm);
    }

    @Test
    public void cancelDelegatesToTheRepositories() {
        Alarm alarm = mock(Alarm.class);
        when(alarm.getId()).thenReturn(UUID.randomUUID());

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
