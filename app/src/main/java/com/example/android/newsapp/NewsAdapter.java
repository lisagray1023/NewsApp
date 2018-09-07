package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Custom adapter to create a new News object and set it in a list view
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String TIME_SEPERATOR = "T";

    /**
     * Create a new News object
     * @param context the current context the adapter is being created in
     * @param articles is the list of News objects to be displayed
     */

    public NewsAdapter(Context context, List<News> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if the existing view is being reused, otherwise inflate the view
        View newsArticleView = convertView;
        if(newsArticleView == null) {
            newsArticleView = LayoutInflater.from(getContext()).inflate(R.layout.news_article, parent, false);
        }

        //Get the News object at this position in the list
        News currentArticle = getItem(position);

        //Get the title String from the News object and store it in a variable
        String title = currentArticle.getTitle();
        //Find the title view
        TextView titleView = newsArticleView.findViewById(R.id.title);
        //Set the title string on the Text view
        titleView.setText(title);

        //Get the section String from the News object and store it in a variable
        String section = currentArticle.getSection();
        //Find the section view
        TextView sectionView = newsArticleView.findViewById(R.id.section);
        //Set the section string on the TextVeiew
        sectionView.setText(section);

        //Get the date String from the News object and store it in a variable
        String date = currentArticle.getDate();
        String formattedDate;
        if (date.contains(TIME_SEPERATOR)) {
            String[] parts = date.split(TIME_SEPERATOR);
            formattedDate = parts[0];
        } else formattedDate = date;

        //Find the date view
        TextView dateView = newsArticleView.findViewById(R.id.date);
        //Set the date string on the TextView
        dateView.setText(formattedDate);

        //Return the News object containing the 3 textviews so it can be shows on the ListView
        return newsArticleView;

    }
}
