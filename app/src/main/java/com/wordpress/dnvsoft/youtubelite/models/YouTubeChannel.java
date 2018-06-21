package com.wordpress.dnvsoft.youtubelite.models;

import java.util.List;

public class YouTubeChannel implements YouTubeItem {

    private String Id;
    private String name;
    private String uploadsId;
    private String thumbnailURL;
    private String thumbnailMaxResUrl;
    private String bannerUrl;
    private List<String> featuredChannelsUrls;
    private String description;
    private String subscriptionId;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUploadsId() {
        return uploadsId;
    }

    public void setUploadsId(String uploadsId) {
        this.uploadsId = uploadsId;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getThumbnailMaxResUrl() {
        return thumbnailMaxResUrl;
    }

    public void setThumbnailMaxResUrl(String thumbnailMaxResUrl) {
        this.thumbnailMaxResUrl = thumbnailMaxResUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public List<String> getFeaturedChannelsUrls() {
        return featuredChannelsUrls;
    }

    public void setFeaturedChannelsUrls(List<String> featuredChannelsUrls) {
        this.featuredChannelsUrls = featuredChannelsUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
