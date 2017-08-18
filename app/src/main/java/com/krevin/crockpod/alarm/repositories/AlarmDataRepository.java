package com.krevin.crockpod.alarm.repositories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.krevin.crockpod.alarm.Alarm;

import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class AlarmDataRepository {

    private static final String REPO_KEY = AlarmDataRepository.class.getCanonicalName();

    private Context mContext;
    private SharedPreferences mAlarmSharedPrefs;

    AlarmDataRepository(Context context) {
        this.mContext = context;
        this.mAlarmSharedPrefs = context.getSharedPreferences(REPO_KEY, Context.MODE_PRIVATE);
    }

    List<Alarm> all() {
        return mAlarmSharedPrefs
                .getAll()
                .entrySet()
                .stream()
                .map(e -> buildAlarm(e.getValue().toString()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(a -> a.getNextTriggerTime().getMinuteOfDay()))
                .collect(Collectors.toList());
    }

    void add(Alarm alarm) {
        mAlarmSharedPrefs
                .edit()
                .putString(String.valueOf(alarm.hashCode()), alarm.getIntent().toUri(Intent.URI_INTENT_SCHEME))
                .apply();
    }

    void remove(Alarm alarm) {
        mAlarmSharedPrefs.edit().remove(String.valueOf(alarm.hashCode())).apply();
    }

    private Alarm buildAlarm(String intentUri) {
        try {
            return new Alarm(mContext, parseIntentUri(intentUri));
        } catch (URISyntaxException e) {
            Log.e(REPO_KEY, "Error parsing Intent URI: " + intentUri);
            return null;
        }
    }

    private Intent parseIntentUri(String intentUri) throws URISyntaxException {
        return intentUri.isEmpty() ? null : Intent.parseUri(intentUri, Intent.URI_INTENT_SCHEME);
    }
}
