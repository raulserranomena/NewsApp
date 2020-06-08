package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private static final String TAG = "NewsAdapter";

    private List<NewsData> mNewsList;
    private Context mContext;

    public NewsAdapter(Context context, List<NewsData> newsList) {
        this.mNewsList = newsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_data_list_item, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the current News Data that was clicked on
                NewsData currentNewsData = mNewsList.get(myViewHolder.getLayoutPosition());

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUrl = Uri.parse(currentNewsData.getNewsWebUrl());

                // Create a new intent to view the News URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);

                // Send the intent to launch a new activity
                mContext.startActivity(websiteIntent);

            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        //we use the Glide library to look and download the image from the URL save within our NewsList
        // and set it within the holder News Image View
        Glide.with(mContext)
                .asBitmap()
                .load(mNewsList.get(position).getNewsThumbnailLink())
                .into(holder.mNewsImage);

        holder.mNewsTitle.setText(mNewsList.get(position).getNewsTitle());
        holder.mNewsText.setText(mNewsList.get(position).getNewsTrailText());

    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mNewsTitle, mNewsText;
        private ImageView mNewsImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mNewsTitle = itemView.findViewById(R.id.news_title_text_view);
            mNewsText = itemView.findViewById(R.id.news_text_view);
            mNewsImage = itemView.findViewById(R.id.news_image_view);
        }
    }

    //method to clear the news Data within the adapter
    void clear() {
        mNewsList.clear();
        this.notifyDataSetChanged();
    }

    //method to add the news Data to the adapter
    void addAll(List<NewsData> newsList) {
        mNewsList = newsList;
        this.notifyDataSetChanged();

    }

}
