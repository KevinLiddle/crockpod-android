package com.krevin.crockpod.alarm;

import android.view.View;

import com.krevin.crockpod.R;
import com.krevin.crockpod.TestCrockpodApp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestCrockpodApp.class)
public class SetAlarmActivityTest {

    @Test
    public void theTimePickerIsGoneWhenEditingTheSearchField() {
        SetAlarmActivity activity = Robolectric.buildActivity(SetAlarmActivity.class).create().get();
        View searchField = activity.findViewById(R.id.podcast_rss_feed);
        View timePicker = activity.findViewById(R.id.time_picker);

        searchField.requestFocus();
        assertEquals(View.GONE, timePicker.getVisibility());

        searchField.clearFocus();
        assertEquals(View.VISIBLE, timePicker.getVisibility());
    }
}
