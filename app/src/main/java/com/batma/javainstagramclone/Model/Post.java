package com.batma.javainstagramclone.Model;

public class Post {
    //diger siniflardan ulasmak icin public yaptim
    public String postId;
    public String email;
    public String comment;
    public String downloadUrl;
    public String publisher;

    public Post() {
    }

    public Post(String postId, String email, String comment, String downloadUrl, String publisher) {
        this.postId = postId;
        this.email = email;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
