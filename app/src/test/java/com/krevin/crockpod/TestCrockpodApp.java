package com.krevin.crockpod;

import static org.mockito.Mockito.mock;

public class TestCrockpodApp extends CrockpodApp {

    private HttpClient httpClient;

    @Override
    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = mock(HttpClient.class);
        }
        return httpClient;
    }
}
