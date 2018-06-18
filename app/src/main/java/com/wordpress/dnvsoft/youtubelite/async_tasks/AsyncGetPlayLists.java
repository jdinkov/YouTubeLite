package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetPlayLists extends AsyncYoutube {

    private String channelId;
    private String pageToken;

    public AsyncGetPlayLists(Context c, String channelId, String pageToken, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
        this.pageToken = pageToken;

    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubePlayList playlist;
        ArrayList<YouTubePlayList> youTubePlayLists = new ArrayList<>();

        YouTube.Playlists.List playListRequest = youtube.playlists().list("snippet");
        playListRequest.setFields("items(id,snippet(title,thumbnails/medium/url)),nextPageToken");
        playListRequest.setChannelId(channelId);
        playListRequest.setPageToken(pageToken);
        playListRequest.setMaxResults(20L);
        if (accountEmail == null) {
            playListRequest.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        PlaylistListResponse response = playListRequest.execute();
        result.setNextPageToken(response.getNextPageToken());
        int size = response.getItems().size();
        for (int i = 0; i < size; i++) {
            playlist = new YouTubePlayList();
            playlist.setId(response.getItems().get(i).getId());

            playlist.setName(response.getItems().get(i).getSnippet().getTitle());
            playlist.setThumbnailURL(response.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            youTubePlayLists.add(playlist);
        }

        result.setYouTubePlayLists(youTubePlayLists);

        return result;
    }
}
