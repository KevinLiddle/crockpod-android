package com.krevin.crockpod;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class AutoCompleteSearchViewTest {

    private ArrayAdapter adapter;
    private AutoCompleteSearchView view;

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        adapter = mock(ArrayAdapter.class);
        Filter filter = mock(Filter.class);
        when(adapter.getFilter()).thenReturn(filter);

        view = new AutoCompleteSearchView(context, null);
    }

    @Test
    public void performFilteringCallsTheSearchFunctionAndAddsResultsToAdapter() {
        final String[] searchText = {""};
        List results = Arrays.asList("result1", "result2");
        AutoCompleteSearchView.SearchFunction<List> searchFunc = (text, callback) -> {
            searchText[0] += text;
            callback.accept(results);
        };

        view.init(adapter, searchFunc);
        view.performFiltering("search text", 0);

        assertEquals("search text", searchText[0]);
        verify(adapter).clear();
        verify(adapter).addAll(results);
    }

    @Test
    public void performFilteringDoesNotCallSearchFuncIfTextIsTooShort() {
        List<String> searchCalls = new ArrayList();
        view.init(adapter, (text, callback) -> searchCalls.add("called"));

        view.performFiltering("ab", 0);
        assertEquals(0, searchCalls.size());

        view.performFiltering("abc", 0);
        assertEquals(1, searchCalls.size());
    }
}
