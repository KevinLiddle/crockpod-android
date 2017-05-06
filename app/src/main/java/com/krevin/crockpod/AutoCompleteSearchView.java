package com.krevin.crockpod;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.List;
import java.util.function.Consumer;

public class AutoCompleteSearchView<T> extends AutoCompleteTextView {
    private final Context mContext;
    private SearchFunction<List<T>> mSearchFunc;
    private ArrayAdapter<T> mAdapter;

    public AutoCompleteSearchView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
    }

    public void init(ArrayAdapter<T> adapter, SearchFunction<List<T>> searchFunc) {
        mSearchFunc = searchFunc;
        mAdapter = adapter;
        setAdapter(mAdapter);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (text.length() >= 3) {
            mSearchFunc.call(
                    text.toString(),
                    results -> {
                        mAdapter.clear();
                        mAdapter.addAll(results);
                        super.performFiltering(text, keyCode);
                    });
        } else {
            super.performFiltering(text, keyCode);
        }
    }

    @FunctionalInterface
    public interface SearchFunction<U extends List> {
        void call(String text, Consumer<U> callback);
    }
}
