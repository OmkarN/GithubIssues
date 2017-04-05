package com.omkarneogi.www.githubissues;

/**
 * Created by omkar on 4/2/17.
 */

public class Comment {
    String commentBody;
    String username;

    public Comment(String commentBody, String username) {
        this.commentBody = commentBody;
        this.username = username;
    }

    public Comment() {
        // Empty Constructor
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
