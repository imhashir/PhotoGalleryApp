/*This file contains a lot of commented code. The working of that code is replaced by Picasso library
* and I just couldn't delete that whole code because it took me two days understanding and writing
* that shit down. All that code is fully working. To implement that code, just comment Picasso Library
* implementation and uncomment everything else. */
package com.hashirbaig.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment{

    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private PhotoAdapter mAdapter;
    //private ThumbnailDownloader mThumbnailDownloader;
    private static int page = 0;

    public static Fragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        setHasOptionsMenu(true);
        /*
        Handler handler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(handler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                if(isAdded()) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    photoHolder.bindImage(drawable);
                }
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        */
        Log.i(TAG, "Background thread Started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), new Integer(getResources().getInteger(R.integer.no_of_cols))));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(1)) {
                    //mThumbnailDownloader.clearPreloadQueue();
                    new FetchItemsTask().execute();
                }
            }
        });

        setupAdapter();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mThumbnailDownloader.cleanQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter () {

        if(isAdded()) {
            if(mAdapter == null) {
                mAdapter = new PhotoAdapter(mItems);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

        private static final String TAG = "AsyncTask";
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            String query = "cats";
            page++;
            if(query == null)
                return new FlickrFetchr().getRecentFlickr(page);
            else
                return new FlickrFetchr().getSearchResults(query, page);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            if(isAdded()) {
                mItems.addAll(items);
                setupAdapter();
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{

        private ImageView mGalleryImage;
        private GalleryItem mImage;

        public PhotoHolder(View itemView) {
            super(itemView);
            mGalleryImage = (ImageView) itemView.findViewById(R.id.gallery_item_image);
        }

        public void bindImage(GalleryItem item) {
            Picasso.with(getActivity())
                    .load(item.getUrl())
                    .into(mGalleryImage);
            //mGalleryImage.setImageDrawable(drawable);
        }

        public ImageView getImageView() {
            return mGalleryImage;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mImages;

        public PhotoAdapter(List<GalleryItem> images) {
            mImages = images;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.gallery_item, parent, false);

            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            /*
            GalleryItem item = mItems.get(position);
            mThumbnailDownloader.queueThumbnail(holder, item.getUrl());
            try {
                preloadThumbnails(position);
            } catch (IOException ioe) {
                Log.e(TAG, "Thumbnail couldn't be preloaded");
            }
            */
            holder.bindImage(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }
    /*
    void preloadThumbnails(int position) throws IOException{
        final int THRESHOLD = 10;
        for(int x = position - THRESHOLD; x < position + THRESHOLD; x++) {
            if(x >= 0 && x < mItems.size())
                mThumbnailDownloader.queuePreload(mItems.get(x).getUrl());
        }
    }
    */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
    }
}