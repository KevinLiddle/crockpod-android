package com.krevin.crockpod.alarm.repositories;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.alarm.Alarm;
import com.krevin.crockpod.alarm.AlarmReceiver;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class AlarmServiceRepositoryTest {

    private ShadowAlarmManager shadowAlarmManager;
    private AlarmServiceRepository repo;
    private DateTime triggerTime;
    private Alarm alarm;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(alarmManager);
        repo = new AlarmServiceRepository(context);

        alarm = mock(Alarm.class);
        triggerTime = DateTime.now();

        when(alarm.getNextTriggerTime()).thenReturn(triggerTime);
    }

    @Test
    public void setAddsTheAlarmIntent() {
        repo.set(alarm);

        ShadowAlarmManager.ScheduledAlarm nextAlarm = shadowAlarmManager.getNextScheduledAlarm();
        ShadowPendingIntent shadowPendingIntent = shadowOf(nextAlarm.operation);

        Intent expectedIntent = new Intent(context, AlarmReceiver.class);
        expectedIntent.putExtra(Alarm.ALARM_ID_KEY, alarm.getId());

        assertEquals(triggerTime.getMillis(), nextAlarm.triggerAtTime);
        assertTrue(expectedIntent.filterEquals(shadowPendingIntent.getSavedIntent()));
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT, shadowPendingIntent.getFlags());
    }

    @Test
    public void cancelRemovesTheAlarm() {
        repo.set(alarm);
        assertEquals(1, shadowAlarmManager.getScheduledAlarms().size());

        repo.cancel(alarm);
        assertEquals(0, shadowAlarmManager.getScheduledAlarms().size());
    }
}
