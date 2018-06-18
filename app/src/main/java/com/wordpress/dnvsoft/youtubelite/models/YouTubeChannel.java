package com.wordpress.dnvsoft.youtubelite.models;

public class YouTubeChannel implements YouTubeItem {

    private String Id;
    private String name;
    private String uploadsId;
    private String thumbnailURL;
    private String thumbnailMaxResUrl;
    private String bannerUrl;

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
}
