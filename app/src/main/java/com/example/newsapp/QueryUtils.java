package com.example.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils extends AsyncTaskLoader<List<NewsData>> {

    private static final String TAG = "QueryUtils";

    public QueryUtils(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<NewsData> loadInBackground() {
        String jsonResponse = makeHttpRequest(getUrl());
        List<NewsData> newsList = extractFeatureFromJson(jsonResponse);
        return newsList;
    }


    private List<NewsData> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news Data to
        List<NewsData> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);


            // Extract the JSONObject associated with the key called "response"
            JSONObject jsonResponse = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news.
            JSONArray newsArray = jsonResponse.getJSONArray("results");

            // For each news Data in the newsArray, create an {@link NewsData} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news Data at position i within the list of news
                JSONObject currentNewsData = newsArray.getJSONObject(i);

                //For a given news Data, extract the value for the key called "webTitle"
                String title = currentNewsData.getString("webTitle");

                //For a given news Data, extract the value for the key called "webPublicationDate"
                String date = currentNewsData.getString("webPublicationDate");

                //For a given news Data, extract the value for the key called "webUrl"
                String url = currentNewsData.getString("webUrl");

                // For a given news Data, extract the JSONArray associated with the
                // key called "tags"
                JSONArray tags = currentNewsData.getJSONArray("tags");

                String tag;
                //Get the JSONObject of the first tag if possible
                if (tags.length() == 0) {
                    // if the tags array is empty, extract the value for the key "sectionName" of the given news Data
                    tag = currentNewsData.getString("sectionName");
                } else {
                    //if the tags array is not empty, then extract the value for the key "webTitle"
                    //of the first JSONObject within the tags array
                    tag = tags.getJSONObject(0).getString("webTitle");
                }

                // Get the JSONObject associated with the key "fields"
                JSONObject fields = currentNewsData.getJSONObject("fields");

                // Extract the value for the key called "trailText"
                String text = fields.getString("trailText");

                // Extract the value for the key called "thumbnail"
                String thumbnail = fields.getString("thumbnail");


                // Create a new {@link NewsData} object with the tag, title, url, date, text, thumbnail
                // and url from the JSON response.
                NewsData newsData = new NewsData(tag, title, url, date, text, thumbnail);

                // Add the new {@link NewsData} to the list of News.
                newsList.add(newsData);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News Data JSON results", e);
        }

        // Return the list of News
        return newsList;
    }

    private String makeHttpRequest(URL url) {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.d(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "makeHttpRequest: Error closing inputStream", e);
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private URL getUrl() {
        URL url = null;

        try {
            url = new URL(createUrlString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "getUrl: Problem generating the URL for the httpRequest", e);

        }
        return url;
    }

    private String createUrlString() {
        return new Uri.Builder()
                .scheme("https")
                .encodedAuthority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("q", "android")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("show-tags", "keyword")
                .appendQueryParameter("show-fields", "trailText,byline,thumbnail")
                .appendQueryParameter("api-key", "1ede8549-aed4-4819-9f67-8d5bcc593b31")
                .build()
                .toString();
    }

    public static boolean isNetworkActive(Context context) {
        // Check for connectivity status
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
