package com.krevin.crockpod.podcast;

import com.android.volley.Response;
import com.krevin.crockpod.BuildConfig;
import com.krevin.crockpod.HttpClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastSearchTest {

    private HttpClient httpClient;
    private PodcastSearch podcastSearch;

    @Before
    public void setUp() throws Exception {
        httpClient = mock(HttpClient.class);
        podcastSearch = new PodcastSearch(httpClient);
    }

    @Test
    public void searchCancelsAllOtherRequestsOnTheQueue() {
        podcastSearch.search("something", podcasts -> {});

        verify(httpClient).cancelAllRequests();
    }

    @Test
    public void searchRequestsASearchWithTheTermFromItunes() {
        podcastSearch.search("something", podcasts -> {});

        String expectedUrl = "https://itunes.apple.com/search?media=podcast&attribute=titleTerm&limit=3&term=something";
        verify(httpClient).getJson(eq(expectedUrl), any(Response.Listener.class), any(Response.ErrorListener.class));
    }

    @Test
    public void searchParsesResultsAndPassesThemToResponseCallback() throws Exception {
        List<Podcast> results = new ArrayList<>();

        JSONObject jsonResponse = new JSONObject()
                .put("results", new JSONArray(Collections.singletonList(new JSONObject()
                        .put("collectionName", "Purple Rain")
                        .put("feedUrl", "lakeminnetonka.com/feed")
                        .put("artistName", "Prince")
                        .put("artworkUrl100", "lakeminnetonka.com/art")))
                );
        doAnswer(args -> {
            Response.Listener<JSONObject> l = args.getArgument(1);
            l.onResponse(jsonResponse);
            return null;
        }).when(httpClient).getJson(anyString(), any(), any());

        podcastSearch.search("something", results::addAll);

        assertEquals(1, results.size());
        Podcast actual = results.get(0);
        assertEquals("Purple Rain", actual.getName());
        assertEquals("Prince", actual.getAuthor());
        assertEquals("lakeminnetonka.com/feed", actual.getRssFeedUrl());
        assertEquals("lakeminnetonka.com/art", actual.getLogoUrl());
    }
}
