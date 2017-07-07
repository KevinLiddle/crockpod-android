package com.krevin.crockpod;

import android.content.Context;

import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.UniqueIntId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UniqueIntIdTest {
    @Test
    public void generateReturnsUniqueIntegerIds() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        assertEquals(1, UniqueIntId.generate(context));
        assertEquals(2, UniqueIntId.generate(context));
        assertEquals(3, UniqueIntId.generate(context));
        assertEquals(4, UniqueIntId.generate(context));
    }
}
