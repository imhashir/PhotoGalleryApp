package com.hashirbaig.android.photogallery;

import android.os.HandlerThread;
import android.util.Log;

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader(){
        super(TAG);
    }

    public void queueThumbnail(T traget, String url) {
        Log.i(TAG, "Got the URL: " + url);
    }

}
