package com.hashirbaig.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private static int page = 1;
    private List<GalleryItem> mItems = new ArrayList<>();
    private FlickrFetchr mFetcher;
    private PhotoAdapter mAdapter;

    public static Fragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFetcher = new FlickrFetchr();
        new FetchItemsTask().execute();
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
                    page++;
                    new FetchItemsTask().execute();
                }
            }
        });

        setupAdapter();
        return v;
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
            return mFetcher.fetchItems(page);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems.addAll(items);
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{

        private TextView mImageCaption;
        private GalleryItem mImage;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageCaption = (TextView) itemView;
        }

        public void bindImage(GalleryItem item) {
            mImage = item;
            mImageCaption.setText(mImage.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mImages;

        public PhotoAdapter(List<GalleryItem> images) {
            mImages = images;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());

            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.bindImage(mImages.get(position));
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }
}
