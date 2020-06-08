package com.example.newsapp;

public class NewsData {

    private String newsTag;
    private String newsTitle;
    private String newsWebUrl;
    private String newsPublicationDate;
    private String newsTrailText;
    private String newsThumbnailLink;

    public NewsData(String newsTag, String newsTitle, String newsWebUrl, String newsPublicationDate, String newsTrailText, String newsThumbnailLink) {
        this.newsTag = newsTag;
        this.newsTitle = newsTitle;
        this.newsWebUrl = newsWebUrl;
        this.newsPublicationDate = newsPublicationDate;
        this.newsTrailText = newsTrailText;
        this.newsThumbnailLink = newsThumbnailLink;
    }

    public String getNewsTag() {
        return newsTag;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsWebUrl() {
        return newsWebUrl;
    }

    public String getNewsPublicationDate() {
        return newsPublicationDate;
    }

    public String getNewsTrailText() {
        return newsTrailText;
    }

    public String getNewsThumbnailLink() {
        return newsThumbnailLink;
    }
}



