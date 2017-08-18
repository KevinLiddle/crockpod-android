package com.krevin.crockpod.alarm;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.krevin.crockpod.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SetAlarmActivityTest {

    @Test
    public void theKeyboardIsClosedWhenLosingFocusOfSearchField() {
        SetAlarmActivity activity = Robolectric.buildActivity(SetAlarmActivity.class).create().get();
        View searchField = activity.findViewById(R.id.podcast_rss_feed);
        View timePicker = activity.findViewById(R.id.time_picker);
        InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        searchField.requestFocus();
        assertEquals(View.GONE, timePicker.getVisibility());
        assertTrue(keyboard.isActive());

        searchField.clearFocus();
        assertEquals(View.VISIBLE, timePicker.getVisibility());
        assertFalse(keyboard.isActive());
    }
}
