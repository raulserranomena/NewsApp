package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    //Loader ID
    private static final int NEWS_LOADER_ID = 0;

    //List of News
    private List<NewsData> mNewsList = new ArrayList<>();

    //RecyclerView that will hold the NewsList
    private RecyclerView mNewsListRecyclerView;

    //NewsAdapter for the RecyclerViewList
    private NewsAdapter mAdapter;

    //EmptyStateTextView for the RecyclerView
    private TextView mEmptyStateTextView;

    //SwipeRefreshLayout
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        //Find the reference of the swipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        //Set the OnRefreshListener to the swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);

        //Find the reference of the EmptyStateTextView
        mEmptyStateTextView = findViewById(R.id.empty_state_text_view);

        //Find the reference of the NewsListRecyclerView
        mNewsListRecyclerView = findViewById(R.id.news_list_recycler_view);

        //Create a new LinearLayoutManager for the NewsListRecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //Set the LinearLayoutManager to the NewsListRecyclerView
        mNewsListRecyclerView.setLayoutManager(linearLayoutManager);

        //Create a new NewsAdapter for the NewsListRecyclerView
        mAdapter = new NewsAdapter(this, mNewsList);
        //Set the Adapter for the NewListRecyclerView
        mNewsListRecyclerView.setAdapter(mAdapter);

        //Initial Refresh to load NewsData
        onRefresh();

    }


    @NonNull
    @Override
    public Loader<List<NewsData>> onCreateLoader(int id, @Nullable Bundle args) {
        swipeRefreshLayout.setRefreshing(true);
        return new QueryUtils(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsData>> loader, List<NewsData> newsList) {
        Log.d(TAG, "onLoadFinished: called");

        mNewsList = newsList;
        mAdapter.clear();
        mAdapter.addAll(mNewsList);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsData>> loader) {

    }

    @Override
    public void onRefresh() {

        if (QueryUtils.isNetworkActive(this)) {
            Log.d(TAG, "onRefresh: called, There's Internet");

            androidx.loader.app.LoaderManager.getInstance(this).restartLoader(NEWS_LOADER_ID, null, this);
            mNewsListRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);

        } else {
            Log.d(TAG, "onRefresh: called, No Internet");

            swipeRefreshLayout.setRefreshing(false);
            mNewsListRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);

        }
    }
}