package com.example.ngonotification;

import android.text.Spanned;

public class NotificationModel {

    private String title, body, urlSupport;

    private Spanned url;

    public String getUrlSupport() {
        return urlSupport;
    }

    public void setUrlSupport(String urlSupport) {
        this.urlSupport = urlSupport;
    }

    public Spanned getUrl() {
        return url;
    }

    public void setUrl(Spanned url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
