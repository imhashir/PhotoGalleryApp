package com.hashirbaig.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String QUERY_SERACH_PREFERENCE = "com.hashirbaig.android.photogallery.QueryPreferences.search";
    private static final String QUERY_POLL_PREFERENCE = "com.hashirbaig.android.photogallery.QueryPreferences.lastResultId";
    private static final String QUERY_ALARM_ON = "com.hashirbaig.android.photogallery.QueryPreferences.isAlarmOn";

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

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(QUERY_ALARM_ON, isOn)
                .apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(QUERY_ALARM_ON, false);
    }
}
