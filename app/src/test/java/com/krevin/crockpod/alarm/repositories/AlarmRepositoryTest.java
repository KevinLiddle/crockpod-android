package com.krevin.crockpod.alarm.repositories;

import com.krevin.crockpod.alarm.Alarm;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlarmRepositoryTest {

    private AlarmDataRepository dataRepo;
    private AlarmServiceRepository serviceRepo;
    private AlarmRepository repo;
    private Alarm alarm;

    @Before
    public void setUp() {
        dataRepo = mock(AlarmDataRepository.class);
        serviceRepo = mock(AlarmServiceRepository.class);
        repo = new AlarmRepository(dataRepo, serviceRepo);
        alarm = mock(Alarm.class);
    }

    @Test
    public void setAddsTheAlarmToDataAndServiceRepositoriesIfEnabled() {
        when(alarm.isEnabled()).thenReturn(true);
        repo.set(alarm);

        verify(dataRepo).upsert(alarm);
        verify(serviceRepo).set(alarm);
    }

    @Test
    public void setAddsTheAlarmToDataRepositoryAndCancelsItInServiceRepositoryIfDisabled() {
        when(alarm.isEnabled()).thenReturn(false);
        repo.set(alarm);

        verify(dataRepo).upsert(alarm);
        verify(serviceRepo).cancel(alarm);
    }

    @Test
    public void findDelegatesToTheDataRepository() {
        Alarm alarm = mock(Alarm.class);
        when(dataRepo.find("uuid")).thenReturn(alarm);

        Alarm found = repo.find("uuid");

        assertEquals(found, alarm);
    }

    @Test
    public void cancelDelegatesToTheRepositories() {
        repo.cancel(alarm);

        verify(serviceRepo).cancel(alarm);
        verify(dataRepo).remove(alarm);
    }

    @Test
    public void listDelegatesToTheDataRepository() {
        List<Alarm> expected = Collections.singletonList(alarm);
        when(dataRepo.all()).thenReturn(expected);

        List<Alarm> actual = repo.list();

        assertEquals(expected, actual);
    }
}
