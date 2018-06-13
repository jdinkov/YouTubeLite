package com.wordpress.dnvsoft.youtubelite.models;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoItemWrapper implements Serializable {

    private ArrayList<YouTubeVideo> itemDetails;

    public VideoItemWrapper(ArrayList<YouTubeVideo> items) {
        this.itemDetails = items;
    }

    public ArrayList<YouTubeVideo> getItems() {
        return itemDetails;
    }
}
