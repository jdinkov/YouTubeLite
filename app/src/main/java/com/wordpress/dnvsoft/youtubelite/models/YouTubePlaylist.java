package com.wordpress.dnvsoft.youtubelite.models;

public class YouTubePlayList implements YouTubeItem {

    private String Id;
    private String name;
    private String thumbnailURL;
    private String thumbnailMaxResUrl;

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
}
