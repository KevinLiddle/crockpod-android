package com.krevin.crockpod;

import android.content.Context;
import android.content.SharedPreferences;

class UniqueIntId {

    private static final String PREFS_KEY = UniqueIntId.class.getCanonicalName();

    static int generate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        int nextId = sharedPreferences.getInt(PREFS_KEY, 0) + 1;

        sharedPreferences.edit().putInt(PREFS_KEY, nextId).apply();

        return nextId;
    }
}
