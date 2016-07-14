package com.hashirbaig.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String QUERY_SERACH_PREFERENCE = "com.hashirbaig.android.photogallery.QueryPreferences.search";

    public static void setQueryString (Context context, String string) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(QUERY_SERACH_PREFERENCE, string)
                .apply();
    }

    public static String getQueryString(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(QUERY_SERACH_PREFERENCE, null);
    }

}
