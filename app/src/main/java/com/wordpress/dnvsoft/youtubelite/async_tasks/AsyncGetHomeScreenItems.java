package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetHomeScreenItems extends AsyncYoutube {

    private String pageToken;

    public AsyncGetHomeScreenItems(Context c, String pageToken, TaskCompleted callback) {
        super(c, callback);
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubeVideo video;
        ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();

        YouTube.Videos.List videoList = youtube.videos().list("snippet");
        videoList.setFields("items(id,snippet(title,thumbnails/medium/url)),nextPageToken");
        videoList.setChart("mostPopular");
        videoList.setPageToken(pageToken);

        SharedPreferences preferences = getAppContext().getSharedPreferences("COUNTRY_REGION_CODE", Context.MODE_PRIVATE);
        String regionCode = preferences.getString("REGION_CODE", null);
        if (regionCode == null){
            TelephonyManager manager = (TelephonyManager) getAppContext().getSystemService(getAppContext().TELEPHONY_SERVICE);
            regionCode = manager.getSimCountryIso();
        }
        
        videoList.setRegionCode(regionCode);
        videoList.setMaxResults(20L);
        if (accountEmail == null) {
            videoList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        VideoListResponse response = videoList.execute();
        pageToken = response.getNextPageToken();
        int size = response.getItems().size();
        for (int i = 0; i < size; i++) {
            video = new YouTubeVideo();
            video.setId(response.getItems().get(i).getId());
            video.setName(response.getItems().get(i).getSnippet().getTitle());
            video.setThumbnailURL(response.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            youTubeVideos.add(video);
        }

        result.setNextPageToken(pageToken);
        result.setYouTubeVideos(youTubeVideos);

        return result;
    }
}
