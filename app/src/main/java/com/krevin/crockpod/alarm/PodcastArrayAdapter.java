package com.krevin.crockpod.alarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.krevin.crockpod.R;
import com.krevin.crockpod.podcast.Podcast;

class PodcastArrayAdapter extends ArrayAdapter<Podcast> {

    private final ImageLoader mImageLoader;

    PodcastArrayAdapter(@NonNull Context context, ImageLoader imageLoader) {
        super(context, R.layout.podcast_search_item);
        mImageLoader = imageLoader;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = getView(convertView, parent);
        TextView titleView = view.findViewById(R.id.podcast_search_item_title);
        TextView authorView = view.findViewById(R.id.podcast_search_item_author);
        NetworkImageView logoView = view.findViewById(R.id.podcast_search_item_logo);

        if (position % 2 == 0) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.colorAlmostPrimary, null));
        } else {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary, null));
        }

        Podcast podcast = getItem(position);

        if (podcast != null) {
            titleView.setText(podcast.getName());
            authorView.setText(podcast.getAuthor());
            logoView.setImageUrl(podcast.getLogoUrlSmall(), mImageLoader);
        }
        return view;
    }

    private View getView(View convertView, ViewGroup parent) {
        return convertView == null ?
                LayoutInflater.from(getContext()).inflate(R.layout.podcast_search_item, parent, false) :
                convertView;
    }
}