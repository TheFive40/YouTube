package com.example.youtubeadmin.Model;

public class TransmissionsModel {
    private double views;
    private int comments;
    private int likes;
    private String category, channel;

    public TransmissionsModel(double views, int comments, String category, String channel, int likes) {
        this.views = views;
        this.comments = comments;
        this.likes = likes;
        this.category = category;
        this.channel = channel;
    }

    public double getViews() {
        return views;
    }

    public void setViews(double views) {
        this.views = views;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
