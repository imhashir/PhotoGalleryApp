package com.hashirbaig.android.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService{
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 10;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Checking for new content");
        if(!isNetworkAvailableAndConnected())
            return;

        String query = QueryPreferences.getQueryString(this);
        String lastId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetchr().getRecentFlickr(1);
        } else {
            items = new FlickrFetchr().getSearchResults(query, 1);
        }

        if(items.size() == 0)
            return;

        String resultId = items.get(0).getId();
        if(resultId.equals(lastId)) {
            Log.i(TAG, "No new results");
        } else {
            Log.i(TAG, "New results found");
        }

        QueryPreferences.setLastResultId(this, resultId);
    }

    public static boolean isServiceAlarmOn(Context context) {

        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    public static void setAlarmService(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    POLL_INTERVAL,
                    pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
