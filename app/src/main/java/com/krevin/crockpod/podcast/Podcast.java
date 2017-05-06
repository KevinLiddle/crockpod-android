package com.krevin.crockpod.podcast;

public class Podcast {

    private String mName;
    private String mRssFeedUrl;
    private String mAuthor;
    private String mLogoUrl;

    public Podcast(String name, String rssFeedUrl, String author, String logoUrl) {
        mName = name;
        mRssFeedUrl = rssFeedUrl;
        mAuthor = author;
        mLogoUrl = logoUrl;
    }

    public String getName() {
        return mName;
    }

    public String getRssFeedUrl() {
        return mRssFeedUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getLogoUrl() {
        return mLogoUrl;
    }

    public String toString() {
        return getName();
    }
}
