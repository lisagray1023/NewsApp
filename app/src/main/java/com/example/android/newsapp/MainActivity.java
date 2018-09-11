package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * URL for news data from Guardian API
     */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?q=gender%20equality&show-tags=contributor&api-key=39551a7a-db5c-4688-8765-f48f38d90413";

    /**
     * Adapter for the list of News objects
     */
    private NewsAdapter mAdapter;

    /**
     * Constant value for the NewsLoader
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        //Create new loader for the given URL
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> articles) {
        //Hide progress bar because data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        //Set empty state text to display message
        mEmptyStateTextView.setText(R.string.no_results);

        //Clear adapter of previous news data
        mAdapter.clear();

        //If there's a valid list of News objects, add them to the adapater set,
        //will trigger listView to update
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //Loader reset to clear out existing data
        mAdapter.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find a reference to the ListView in the layoug
        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create a new ArrayAdapater of news articles
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set the adapter on the ListView
        newsListView.setAdapter(mAdapter);

        //Set an item click listener on the ListView, which sends intent to a web browser
        //to open news article on guardians website
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find news article that was clicked on
                News currentArticle = mAdapter.getItem(position);
                //Convert the string URL into a URI object to pass to the intent constructor
                Uri newsUri = Uri.parse(currentArticle.getUrl());
                //Create a new intent to view the news article in a web browser
                Intent websiteInent = new Intent(Intent.ACTION_VIEW, newsUri);
                //Send intent to launch the activity
                startActivity(websiteInent);
            }
        });

        //Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Get a reference to the LoaderManager in order to interact with loaders
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader, pass in the ID constant and null for bundle
            //Pass in activity for the LoaderCallbacks parameter
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            //Otherwise display error, hiding the loading spinner so error msg is vislbe
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with error msg
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
}
