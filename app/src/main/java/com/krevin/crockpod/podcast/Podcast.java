package com.krevin.crockpod.podcast;

public class Podcast {

    private String mName;
    private String mRssFeedUrl;

    public Podcast(String name, String rssFeedUrl) {
        mName = name;
        mRssFeedUrl = rssFeedUrl;
    }

    public String getName() {
        return mName;
    }

    public String getRssFeedUrl() {
        return mRssFeedUrl;
    }

    public String toString() {
        return getName();
    }
}
