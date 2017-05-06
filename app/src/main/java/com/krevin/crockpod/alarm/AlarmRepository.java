package com.krevin.crockpod.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.krevin.crockpod.UniqueIntId;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AlarmRepository {

    private static final String REPO_KEY = AlarmRepository.class.getCanonicalName();

    private Context mContext;
    private SharedPreferences mAlarmSharedPrefs;

    public AlarmRepository(Context context) {
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

    public int add(Alarm alarm) {
        int id = UniqueIntId.generate(mContext);
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
