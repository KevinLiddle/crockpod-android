package com.krevin.crockpod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class HttpClient {

    private static HttpClient mInstance;
    private final RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;

    private HttpClient(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public static HttpClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpClient(context);
        }
        return mInstance;
    }

    public void cancelAllRequests() {
        mRequestQueue.cancelAll(r -> true);
    }

    public void getJson(String url, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        mRequestQueue.add(new JsonObjectRequest(url, null, onSuccess, onError));
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
