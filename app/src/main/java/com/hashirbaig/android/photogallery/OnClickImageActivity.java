package com.hashirbaig.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Hashir on 7/16/2016.
 */
public class OnClickImageActivity extends SingleFragmentActivity{

    public static final String IMAGE_URL = "com.hashirbaig.android.photogallery.OnClickImageActivity";

    @Override
    public Fragment createFragment() {
        return OnClickImageFragment.newInstance();
    }

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, OnClickImageActivity.class);
        intent.putExtra(IMAGE_URL, url);
        return intent;
    }
}
