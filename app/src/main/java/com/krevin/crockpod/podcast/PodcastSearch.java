package com.krevin.crockpod.podcast;

import android.net.Uri;
import android.util.Log;

import com.krevin.crockpod.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PodcastSearch {

    private static final String TAG = PodcastSearch.class.getCanonicalName();
    private static final String PROTOCOL = "https";
    private static final String HOST = "itunes.apple.com";
    private static final String SEARCH_PATH = "search";
    private static final String MEDIA_PARAM_KEY = "media";
    private static final String PODCAST_PARAM = "podcast";
    private static final String TITLE_PARAM_KEY = "attribute";
    private static final String TITLE_PARAM = "titleTerm";
    private static final String LIMIT_PARAM_KEY = "limit";
    private static final String TERM_PARAM_KEY = "term";
    private static final int LIMIT = 3;
    private static final String RESPONSE_TITLE_KEY = "collectionName";
    private static final String RESPONSE_FEED_KEY = "feedUrl";
    private static final String RESPONSE_ARTIST_KEY = "artistName";
    private static final String RESPONSE_ARTWORK_SMALL_KEY = "artworkUrl100";
    private static final String RESPONSE_ARTWORK_LARGE_KEY = "artworkUrl600";
    private static final String RESPONSE_RESULTS_KEY = "results";

    private final HttpClient mHttpClient;

    public PodcastSearch(HttpClient httpClient) {
        mHttpClient = httpClient;
    }

    public void search(String title, final Consumer<List<Podcast>> onResponse) {
        mHttpClient.cancelAllRequests();
        mHttpClient.getJson(
                buildUrl(title),
                response -> onResponse.accept(parseResults(response)),
                error -> Log.e(TAG, "Error searching for podcasts: " + error.getMessage())
        );
    }

    private List<Podcast> parseResults(JSONObject response) {
        List<Podcast> results = new ArrayList<>();
        try {
            JSONArray jsonResults = response.getJSONArray(RESPONSE_RESULTS_KEY);
            for (int i = 0; i < jsonResults.length(); i++) {
                JSONObject json = jsonResults.getJSONObject(i);
                Podcast podcast = new Podcast(
                        json.getString(RESPONSE_TITLE_KEY),
                        json.getString(RESPONSE_FEED_KEY),
                        json.getString(RESPONSE_ARTIST_KEY),
                        json.getString(RESPONSE_ARTWORK_SMALL_KEY),
                        json.getString(RESPONSE_ARTWORK_LARGE_KEY)
                );
                results.add(podcast);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    private String buildUrl(String term) {
        return new Uri.Builder()
                .scheme(PROTOCOL)
                .authority(HOST)
                .path(SEARCH_PATH)
                .appendQueryParameter(MEDIA_PARAM_KEY, PODCAST_PARAM)
                .appendQueryParameter(TITLE_PARAM_KEY, TITLE_PARAM)
                .appendQueryParameter(LIMIT_PARAM_KEY, String.valueOf(LIMIT))
                .appendQueryParameter(TERM_PARAM_KEY, term)
                .build()
                .toString();
    }
}
