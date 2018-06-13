package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetVideos extends AsyncYoutube {

    private String videoID;
    private String pageToken;

    public AsyncGetVideos(Context context, String videoID,
                          String pageToken, TaskCompleted callback) {
        super(context, callback);
        this.videoID = videoID;
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubeVideo video;
        ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();

        YouTube.Search.List searchList = youtube.search().list("snippet");
        searchList.setFields("items(id/videoId,snippet(title,thumbnails/medium/url)),nextPageToken");
        searchList.setPageToken(pageToken);
        searchList.setOrder("relevance");
        searchList.setType("video");
        searchList.setRelatedToVideoId(videoID);
        searchList.setMaxResults((long) 20);
        if (accountEmail == null) {
            searchList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        SearchListResponse searchListResponse = searchList.execute();
        pageToken = searchListResponse.getNextPageToken();
        int size = searchListResponse.getItems().size();
        for (int i = 0; i < size; i++) {
            video = new YouTubeVideo();
            video.setId(searchListResponse.getItems().get(i).getId().getVideoId());

            video.setName(searchListResponse.getItems().get(i).getSnippet().getTitle());
            video.setThumbnailURL(searchListResponse.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            youTubeVideos.add(video);
        }

        result.setNextPageToken(pageToken);
        result.setYouTubeVideos(youTubeVideos);
        return result;
    }
}
