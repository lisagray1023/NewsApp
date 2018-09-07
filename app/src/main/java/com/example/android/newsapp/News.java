package com.example.android.newsapp;

/**
 * News object represents a news article returned by the Guardian API
 */

public class News {

    //Title of the article
    private String mTitle;

    //Section the article belongs to
    private String mSection;

    //Date article was published, if available
    private String mDate;

    //URL to the webpage for the article
    private String mUrl;

    /**
     * Create a new News object
     * @param title is the title of the article
     * @param section is the section the article is located in
     * @param date is the date the article was published, if available
     * @param url is the website URL to where the article is published online
     */

    public News (String title, String section, String date, String url) {
        mTitle = title;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    //Get the title of the News article
    public String getTitle() {return mTitle;}

    //Get the section of the News article
    public String getSection() {return mSection;}

    //Get the publication date of the News article
    public String getDate() {return mDate;}

    //Get the website URL to see the article published online
    public String getUrl() {return mUrl;}
}
