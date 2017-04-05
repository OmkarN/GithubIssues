package com.omkarneogi.www.githubissues;

import android.support.annotation.NonNull;

/**
 * Created by omkar on 4/2/17.
 */

public class Issue implements Comparable<Issue>{
    Double id;
    String title;
    String body;
    String shortBody;
    Long updated_at;
    String comment_url;

    public Issue(Double id, String title, String body, Long updated_at, String comment_url) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.updated_at = updated_at;
        this.comment_url = comment_url;

        if(body.length() > 140) {
            this.shortBody = body.substring(0, 139);
        }
        else {
            this.shortBody = body;
        }
    }
    public Issue () {
        // Empty Constructor

    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }

    public String getComment_url() {
        return comment_url;
    }

    public void setComment_url(String comment_url) {
        this.comment_url = comment_url;
    }

    public String getShortBody() {
        return shortBody;
    }

    public void setShortBody(String body) {
        if(body.length() > 140) {
            this.shortBody = body.substring(0, 139);
        }
        else {
            this.shortBody = body;
        }
    }

    @Override
    public int compareTo(@NonNull Issue issue) {
        return updated_at.compareTo(issue.updated_at);
    }
}
