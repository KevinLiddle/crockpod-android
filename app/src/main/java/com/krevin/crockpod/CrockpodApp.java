package com.krevin.crockpod;

import android.app.Application;


public class CrockpodApp extends Application {
    public HttpClient getHttpClient() {
        return HttpClient.getInstance(this);
    }
}
