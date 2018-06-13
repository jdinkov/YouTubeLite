package com.wordpress.dnvsoft.youtubelite.models;

public interface YouTubeItem {
    String Id = null;
    String name = null;
    String thumbnailURL = null;
    String thumbnailMaxResUrl = null;

    String getId();

    void setId(String Id);

    String getName();

    void setName(String name);

    String getThumbnailURL();

    void setThumbnailURL(String thumbnailURL);

    String getThumbnailMaxResUrl();

    void setThumbnailMaxResUrl(String thumbnailMaxResUrl);
}
