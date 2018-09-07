package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;


/**
 * Loads a list of news articles by using an AsyncTask to perform the network request to the given url
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**Query URL */
    private String mUrl;

    /**
     * Constructs a new NewsLoader
     * @param context of the activity
     * @param url to load data from
     */

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {onForceLoad();}

    /** This is on the background thread */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        //Perform network request, parse response, extract a list of news articles
        List<News> articles = QueryUtils.fetchNewsData(mUrl);
        return articles;
    }
}
