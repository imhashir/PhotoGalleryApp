package com.hashirbaig.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class OnClickImageFragment extends Fragment{

    private static final String TAG = "OnClickImageFragment";

    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String url;
    private Bitmap mBitmap;

    public static Fragment newInstance() {
        return new OnClickImageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getActivity().getIntent().getStringExtra(OnClickImageActivity.IMAGE_URL);
        new DownloadImageTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.on_click_image_layout, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image_view);
        mTextView = (TextView) v.findViewById(R.id.no_image_text);
        mProgressBar = (ProgressBar) v.findViewById(R.id.image_load_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        return v;
    }

    private void setImage() {
        mImageView.setImageBitmap(mBitmap);
        mTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void failedDownloadingImage() {
        mTextView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                byte[] imageBytes = new FlickrFetchr().getUrlBytes(url);
                mBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if(b) {
                setImage();
            } else {
                failedDownloadingImage();
            }
        }
    }
}
