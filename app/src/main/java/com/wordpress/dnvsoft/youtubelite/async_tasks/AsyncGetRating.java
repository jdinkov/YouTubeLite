package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoGetRatingResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetRating extends AsyncYoutube {

    private String videoID;

    public AsyncGetRating(Context c, String id, TaskCompleted callback) {
        super(c, callback);
        this.videoID = id;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        YouTubeVideo video = new YouTubeVideo();
        ArrayList<YouTubeVideo> items = new ArrayList<>();

        YouTube.Videos.GetRating videosRating = youtube.videos().getRating(videoID);
        videosRating.setFields("items");
        if (accountEmail == null) {
            videosRating.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        VideoGetRatingResponse response = videosRating.execute();
        video.setRating(response.getItems().get(0).getRating());
        items.add(video);
        result.setYouTubeVideos(items);

        return result;
    }
}
