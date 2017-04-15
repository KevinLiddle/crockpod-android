package com.krevin.crockpod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmRepository {

    private static final String REPO_KEY = AlarmRepository.class.getCanonicalName();

    private Context context;
    private SharedPreferences alarmSharedPrefs;

    public AlarmRepository(Context context) {
        this.context = context;
        this.alarmSharedPrefs = context.getSharedPreferences(REPO_KEY, Context.MODE_PRIVATE);
    }

    public List<Alarm> all() {
        Map<String, ?> allUris = alarmSharedPrefs.getAll();

        List<Alarm> result = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allUris.entrySet()) {
            String intentUri = entry.getValue().toString();
            try {
                result.add(new Alarm(Integer.valueOf(entry.getKey()), parseIntentUri(intentUri)));
            } catch (URISyntaxException e) {
                Log.e(REPO_KEY, "Error parsing Intent URI: " + intentUri);
            }
        }
        return result;
    }

    public Intent get(int id) {
        String intentUri = alarmSharedPrefs.getString(String.valueOf(id), "");
        try {
            return parseIntentUri(intentUri);
        } catch (URISyntaxException e) {
            Log.e(REPO_KEY, "Error parsing Intent URI: " + intentUri);
            return null;
        }
    }

    public int add(Alarm alarm) {
        int id = UniqueIntId.generate(context);
        alarmSharedPrefs.edit().putString(String.valueOf(id), alarm.getIntent().toUri(Intent.URI_INTENT_SCHEME)).apply();

        return id;
    }

    public void remove(int id) {
        alarmSharedPrefs.edit().remove(String.valueOf(id)).apply();
    }

    private Intent parseIntentUri(String intentUri) throws URISyntaxException {
        return intentUri.isEmpty() ? null : Intent.parseUri(intentUri, Intent.URI_INTENT_SCHEME);
    }
}
