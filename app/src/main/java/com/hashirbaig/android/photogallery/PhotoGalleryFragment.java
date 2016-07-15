/*This file contains a lot of commented code. The working of that code is replaced by Picasso library
* and I just couldn't delete that whole code because it took me two days understanding and writing
* that shit. All that code is fully working. To implement that code, just comment Picasso Library
* implementation and uncomment everything else. */
package com.hashirbaig.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment{

    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private PhotoAdapter mAdapter;
    //private ThumbnailDownloader mThumbnailDownloader;
    private ProgressBar mProgressBar;

    private static int page = 0;

    public static Fragment newInstance() {
        return new PhotoGalleryFragment();
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

        private static final String TAG = "AsyncTask";
        private String mQuery;
        private PhotoGalleryFragment mFragment;

        public FetchItemsTask(PhotoGalleryFragment fragment, String query) {
            mFragment = fragment;
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            page++;

            if(mQuery == null)
                return new FlickrFetchr().getRecentFlickr(page);
            else
                return new FlickrFetchr().getSearchResults(mQuery, page);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {

            if(mProgressBar.getVisibility() == View.VISIBLE)
                mFragment.showProgressBar(false);

            if(isAdded()) {
                if(page == 1) {
                    mItems.clear();
                }
                mItems.addAll(items);
                setupAdapter();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        updateItems();
        Log.i(TAG, "Background thread Started");
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), new Integer(getResources().getInteger(R.integer.no_of_cols))));
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(1)) {
                    //mThumbnailDownloader.clearPreloadQueue();
                    updateItems();
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

    private class PhotoHolder extends RecyclerView.ViewHolder{

        private ImageView mGalleryImage;

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

        final MenuItem searchBox = menu.findItem(R.id.search_item);
        final SearchView searchView = (SearchView) searchBox.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(true);

        MenuItem pollMenu = menu.findItem(R.id.menu_item_start_polling);
        if(PollService.isServiceAlarmOn(getActivity())) {
            pollMenu.setTitle(R.string.stop_polling);
        } else {
            pollMenu.setTitle(R.string.start_polling);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "Query text Submit: " + query);
                showProgressBar(true);
                searchView.onActionViewCollapsed();
                QueryPreferences.setQueryString(getActivity(), query);
                page = 0;
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "Query text changed: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pastQuery = QueryPreferences.getQueryString(getActivity());
                if(pastQuery != null)
                    searchView.setQuery(pastQuery, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clear_search:
                QueryPreferences.setQueryString(getActivity(), null);
                page = 0;
                updateItems();
                return true;
            case R.id.menu_item_start_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setAlarmService(getActivity(), shouldStartAlarm);
                Log.i(TAG, "Polling: " + (shouldStartAlarm ? "Started" : "Stopped"));
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mRecyclerView.setVisibility(!show ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateItems() {
        String query = QueryPreferences.getQueryString(getActivity());
        new FetchItemsTask(this, query).execute();
    }
}