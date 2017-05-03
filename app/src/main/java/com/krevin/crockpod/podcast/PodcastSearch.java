package com.krevin.crockpod.podcast;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PodcastSearch {

    private static final String TAG = PodcastSearch.class.getCanonicalName();
    private static final String URL = "https://itunes.apple.com/search?media=podcast&attribute=titleTerm&limit=%d&term=%s";
    private static final int LIMIT = 3;
    private final RequestQueue requestQueue;

    public PodcastSearch(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
    }

    public List<Podcast> search(String title, final Consumer<List<Podcast>> onResponse) {
        requestQueue.cancelAll(request -> true);
        final List<Podcast> results = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(
                buildUrl(title),
                null,
                response -> onResponse.accept(parseResults(response)),
                error -> Log.e(TAG, "Error searching for podcasts: " + error.getMessage())
        );
        requestQueue.add(request);
        return results;
    }

    private List<Podcast> parseResults(JSONObject response) {
        List<Podcast> results = new ArrayList<>();
        try {
            JSONArray jsonResults = response.getJSONArray("results");
            for (int i = 0; i < jsonResults.length(); i++) {
                JSONObject json = jsonResults.getJSONObject(i);
                results.add(new Podcast(json.getString("collectionName"), json.getString("feedUrl")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private String buildUrl(String term) {
        return String.format(URL, LIMIT, term);
    }
}
