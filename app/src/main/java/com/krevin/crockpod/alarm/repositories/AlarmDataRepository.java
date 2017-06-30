package com.krevin.crockpod.alarm.repositories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.krevin.crockpod.UniqueIntId;
import com.krevin.crockpod.alarm.Alarm;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AlarmDataRepository {

    private static final String REPO_KEY = AlarmDataRepository.class.getCanonicalName();

    private Context mContext;
    private SharedPreferences mAlarmSharedPrefs;

    public AlarmDataRepository(Context context) {
        this.mContext = context;
        this.mAlarmSharedPrefs = context.getSharedPreferences(REPO_KEY, Context.MODE_PRIVATE);
    }

    public List<Alarm> all() {
        return mAlarmSharedPrefs
                .getAll()
                .entrySet()
                .stream()
                .map(e -> buildAlarm(e.getValue().toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Alarm get(int id) {
        return buildAlarm(mAlarmSharedPrefs.getString(String.valueOf(id), ""));
    }

    public int put(Alarm alarm) {
        int id = alarm.getId() == null ? UniqueIntId.generate(mContext) : alarm.getId();
        alarm.setId(id);

        mAlarmSharedPrefs
                .edit()
                .putString(String.valueOf(id), alarm.getIntent().toUri(Intent.URI_INTENT_SCHEME))
                .apply();

        return id;
    }

    public void remove(int id) {
        mAlarmSharedPrefs.edit().remove(String.valueOf(id)).apply();
    }

    private boolean exists(int id) {
        return mAlarmSharedPrefs.contains(String.valueOf(id));
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
