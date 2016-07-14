/*This is fully functional class for downloading Thumbnail from Flickr however this
* class is no more implemented in this app as it's working is replaced by Picasso Library. */

package com.hashirbaig.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener mThumbnailDownloadListener;
    private LruCache<String, Bitmap> mThumbnailCache = new LruCache<>(10 * 1024 * 1024);

    private List<String> mDownloadingUrls;

    public ThumbnailDownloader(Handler handler){
        super(TAG);
        mResponseHandler = handler;
        mDownloadingUrls = new ArrayList<>();
    }

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T targer, Bitmap bitmap);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener) {
        mThumbnailDownloadListener = thumbnailDownloadListener;
    }
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request of message " + mRequestMap.get(target));
                    handleRequest(target);
                } else if(msg.what == MESSAGE_PRELOAD) {
                    try {
                        loadImageToCache((String) msg.obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got the URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else  {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);

            if(url == null) {
                return;
            }
            final Bitmap bitmap;

            if(mThumbnailCache.get(url) == null) {
                loadImageToCache(mRequestMap.get(target));
            }

            bitmap = mThumbnailCache.get(url);
            Log.i(TAG, "Image retrieved from cache");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url) {
                        return;
                    }

                    mRequestMap.remove(target);
                    mDownloadingUrls.remove(url);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Image couldn't be downloaded");
        }
    }

    public void cleanQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    public void clearPreloadQueue() {
        mRequestHandler.removeMessages(MESSAGE_PRELOAD);
    }

    public void loadImageToCache(String url) throws IOException{
        if(mThumbnailCache.get(url) == null && !mDownloadingUrls.contains(url)) {
            mDownloadingUrls.add(url);
            byte[] bytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Log.i(TAG, "Image downloaded successfully");
            mThumbnailCache.put(url, bitmap);
        }
    }

    public void queuePreload(String url) {
        mRequestHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
    }
}
