package com.hashirbaig.android.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService{
    private static final String TAG = "PollService";
    public static final String ACTION_SHOW_NOTIFICATION = "com.hashirbaig.android.photogallery.SHOW_NOTIFICATION";
    private static final long POLL_INTERVAL = 10 * 1000;
    //AlarmManager.INTERVAL_FIFTEEN_MINUTES
    public static final String PERM_PRIVATE = "com.hashirbaig.android.photogallery.PRIVATE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String REQUEST_CODE = "REQUEST_CODE";

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
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pics_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pics_title))
                    .setContentInfo(resources.getString(R.string.new_pics_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            showBackgroundNotification(0, notification);
        }

        QueryPreferences.setLastResultId(this, resultId);
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(NOTIFICATION, notification);
        i.putExtra(REQUEST_CODE, requestCode);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);

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
        QueryPreferences.setAlarmOn(context, isOn);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
