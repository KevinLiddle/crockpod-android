package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.alarm.repositories.AlarmRepository;
import com.krevin.crockpod.podcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class AlarmReceiverTest {

    private Context context;
    private Alarm alarm;
    private AlarmReceiver alarmReceiver;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        Podcast podcast = new Podcast("pname", "pfeed", "pauthor", "plogo");
        alarm = new Alarm(context, podcast, 12, 21);
        alarmReceiver = new AlarmReceiver();
    }

    @Test
    public void onReceiveResetsTheAlarm() {
        alarmReceiver.onReceive(context, alarm.getIntent());

        Alarm resetAlarm = new AlarmRepository(context).list().get(0);
        assertEquals(alarm, resetAlarm);
    }

    @Test
    public void onReceiveStartsTheAlarmRingingActivityWithExtras() {
        alarmReceiver.onReceive(context, alarm.getIntent());
        ShadowApplication shadowApplication = shadowOf(RuntimeEnvironment.application);
        Intent nextActivity = shadowApplication.peekNextStartedActivity();

        assertEquals(AlarmRingingActivity.class.getName(), nextActivity.getComponent().getClassName());
        assertEquals(alarm.getIntent().getExtras().toString(), nextActivity.getExtras().toString());
    }
}
