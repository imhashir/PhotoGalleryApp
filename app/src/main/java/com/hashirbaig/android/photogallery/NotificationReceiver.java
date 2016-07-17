package com.hashirbaig.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver{

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Received result: " + getResultCode());
        if(getResultCode() == Activity.RESULT_OK)
            return;

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION);
        nm.notify(requestCode, notification);
    }
}
