package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

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

/**
 * Helper methods related to requesting and receiving news data from the Guardian APi
 */

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     *  Query the Guardian API and return a list of News objects
     */

    public static List<News> fetchNewsData (String requestURL) {
        //Create URL object
        URL url = createURL(requestURL);

        //Perform the HTTP request to URL and receive JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //Extract relevant fields from the JSON response and create a list of News objects
        List<News> newsArticles = extractFeaturesFromJson(jsonResponse);

        //Return the list of News articles
        return newsArticles;
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //If URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds */);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If request was successful (code 200) then read input stream and parse response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                //Closing input stream could throw an IOException, which is why they
                //makeHttpRequest (URL url) method signature specifies an IOExcpetion could be thrown
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the whole JSON response from the server
     */
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

    /**
     * Return a list of News objects that has been built up from parsing a JSON response.
     */
    private static List<News> extractFeaturesFromJson(String articleJSON) {

        //If the JSON string is empty or null return early
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news articles to
        List<News> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            //Extract the JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            //Extract the JSONArray associated with the key called "results"
            JSONArray articleArray = response.getJSONArray("results");


            //For each news article Object in the articleArray, create a News object
            for (int i = 0; i < articleArray.length(); i++) {
                JSONObject currentArticle = articleArray.getJSONObject(i);

                //Extract the data from the JSONObject
                String title = currentArticle.getString("webTitle");

                //Get JSON Array tags from within results object
                JSONArray tagsArray = currentArticle.getJSONArray("tags");
                JSONObject currentTags = tagsArray.getJSONObject(0);
                String author = currentTags.getString("webTitle");

                String section = currentArticle.getString("sectionName");
                String date = currentArticle.getString("webPublicationDate");
                String url = currentArticle.getString("webUrl");

                //Create a new News object with this data and add it to the articles array
                News article = new News(title, section, date, author, url);
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Guardian API JSON results", e);
        }

        // Return the list of news articles
        return articles;
    }

}
