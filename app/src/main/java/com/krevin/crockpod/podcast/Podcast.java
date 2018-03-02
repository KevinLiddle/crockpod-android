package com.krevin.crockpod.podcast;

public class Podcast {

    private String mName;
    private String mRssFeedUrl;
    private String mAuthor;
    private String mLogoUrlSmall;
    private String mLogoUrlLarge;

    public Podcast(String name, String rssFeedUrl, String author, String logoUrlSmall, String logoUrlLarge) {
        mName = name;
        mRssFeedUrl = rssFeedUrl;
        mAuthor = author;
        mLogoUrlSmall = logoUrlSmall;
        mLogoUrlLarge = logoUrlLarge;
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

    public String getLogoUrlSmall() {
        return mLogoUrlSmall;
    }

    public String getLogoUrlLarge() {
        return mLogoUrlLarge;
    }

    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Podcast podcast = (Podcast) o;

        if (mName != null ? !mName.equals(podcast.mName) : podcast.mName != null)
            return false;
        if (mRssFeedUrl != null ? !mRssFeedUrl.equals(podcast.mRssFeedUrl) : podcast.mRssFeedUrl != null)
            return false;
        if (mAuthor != null ? !mAuthor.equals(podcast.mAuthor) : podcast.mAuthor != null)
            return false;
        if (mLogoUrlSmall != null ? !mLogoUrlSmall.equals(podcast.mLogoUrlSmall) : podcast.mLogoUrlSmall != null)
            return false;
        return mLogoUrlLarge != null ? mLogoUrlLarge.equals(podcast.mLogoUrlLarge) : podcast.mLogoUrlLarge == null;

    }
}
