package com.wordpress.dnvsoft.youtubelite.models;

import java.util.ArrayList;

public class YouTubeResult {

    private boolean isCanceled;
    private String nextPageToken;
    private YouTubeChannel youTubeChannel;
    private ArrayList<YouTubeChannel> youTubeChannels;
    private ArrayList<YouTubeItem> youTubeItems;
    private ArrayList<YouTubeVideo> youTubeVideos;
    private ArrayList<YouTubePlayList> youTubePlayLists;
    private ArrayList<YouTubeCommentThread> commentThread;
    private ArrayList<YouTubeComment> commentReplies;

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public YouTubeChannel getYouTubeChannel() {
        return youTubeChannel;
    }

    public void setYouTubeChannel(YouTubeChannel youTubeChannel) {
        this.youTubeChannel = youTubeChannel;
    }

    public ArrayList<YouTubeChannel> getYouTubeChannels() {
        return youTubeChannels;
    }

    public void setYouTubeChannels(ArrayList<YouTubeChannel> youTubeChannels) {
        this.youTubeChannels = youTubeChannels;
    }

    public ArrayList<YouTubeItem> getYouTubeItems() {
        return youTubeItems;
    }

    public void setYouTubeItems(ArrayList<YouTubeItem> youTubeItems) {
        this.youTubeItems = youTubeItems;
    }

    public ArrayList<YouTubeVideo> getYouTubeVideos() {
        return youTubeVideos;
    }

    public void setYouTubeVideos(ArrayList<YouTubeVideo> youTubeVideos) {
        this.youTubeVideos = youTubeVideos;
    }

    public ArrayList<YouTubePlayList> getYouTubePlayLists() {
        return youTubePlayLists;
    }

    public void setYouTubePlayLists(ArrayList<YouTubePlayList> youTubePlayLists) {
        this.youTubePlayLists = youTubePlayLists;
    }

    public ArrayList<YouTubeCommentThread> getCommentThread() {
        return commentThread;
    }

    public void setCommentThread(ArrayList<YouTubeCommentThread> comments) {
        this.commentThread = comments;
    }

    public ArrayList<YouTubeComment> getCommentReplies() {
        return commentReplies;
    }

    public void setCommentReplies(ArrayList<YouTubeComment> commentReplies) {
        this.commentReplies = commentReplies;
    }
}
