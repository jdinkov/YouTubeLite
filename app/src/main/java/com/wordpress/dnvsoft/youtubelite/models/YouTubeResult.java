package com.wordpress.dnvsoft.youtubelite.models;

import java.util.ArrayList;

public class YouTubeResult {

    private String channelId;
    private String channelUploadsId;
    private boolean isCanceled;
    private String nextPageToken;
    private ArrayList<YouTubeItem> youTubeItems;
    private ArrayList<YouTubeVideo> youTubeVideos;
    private ArrayList<YouTubeCommentThread> commentThread;
    private ArrayList<YouTubeComment> commentReplies;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelUploadsId() {
        return channelUploadsId;
    }

    public void setChannelUploadsId(String channelUploadsId) {
        this.channelUploadsId = channelUploadsId;
    }

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

    public ArrayList<YouTubeItem> getYouTubeItems() {
        return youTubeItems;
    }

    public void setYouTubeItems(ArrayList<YouTubeItem> youTubeVideos) {
        this.youTubeItems = youTubeVideos;
    }

    public ArrayList<YouTubeVideo> getYouTubeVideos() {
        return youTubeVideos;
    }

    public void setYouTubeVideos(ArrayList<YouTubeVideo> youTubeVideos) {
        this.youTubeVideos = youTubeVideos;
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
