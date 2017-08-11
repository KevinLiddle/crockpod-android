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
        return mLogoUrl != null ? mLogoUrl.equals(podcast.mLogoUrl) : podcast.mLogoUrl == null;

    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (mRssFeedUrl != null ? mRssFeedUrl.hashCode() : 0);
        result = 31 * result + (mAuthor != null ? mAuthor.hashCode() : 0);
        result = 31 * result + (mLogoUrl != null ? mLogoUrl.hashCode() : 0);
        return result;
    }
}
