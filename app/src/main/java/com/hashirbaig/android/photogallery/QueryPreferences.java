package com.hashirbaig.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String QUERY_SERACH_PREFERENCE = "com.hashirbaig.android.photogallery.QueryPreferences.search";
    private static final String QUERY_POLL_PREFERENCE = "com.hashirbaig.android.photogallery.QueryPreferences.lastResultId";

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

    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(QUERY_POLL_PREFERENCE, lastResultId)
                .apply();
    }

    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(QUERY_POLL_PREFERENCE, null);
    }
}
