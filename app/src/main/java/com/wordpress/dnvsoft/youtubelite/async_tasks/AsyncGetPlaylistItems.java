package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;
import android.net.Uri;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetPlaylistItems extends AsyncYoutube {

    private String playlistId;
    private String pageToken;

    public AsyncGetPlaylistItems(Context c, String playlistId, String pageToken, TaskCompleted callback) {
        super(c, callback);
        this.playlistId = playlistId;
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubeVideo video;
        ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();

        YouTube.PlaylistItems.List playListItemsList = youtube.playlistItems().list("snippet,status");
        playListItemsList.setFields("items(snippet(title,thumbnails/medium/url,resourceId/videoId),status/privacyStatus),nextPageToken");
        playListItemsList.setPlaylistId(playlistId);
        playListItemsList.setPageToken(pageToken);
        playListItemsList.setMaxResults((long) 20);
        if (accountEmail == null) {
            playListItemsList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        PlaylistItemListResponse response = playListItemsList.execute();
        result.setNextPageToken(response.getNextPageToken());
        int size = response.getItems().size();
        for (int i = 0; i < size; i++) {
            if (!response.getItems().get(i).getStatus().getPrivacyStatus().equals("private")) {
                video = new YouTubeVideo();
                video.setId(response.getItems().get(i).getSnippet().getResourceId().getVideoId());
                video.setName(response.getItems().get(i).getSnippet().getTitle());
                video.setThumbnailURL(response.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
                youTubeVideos.add(video);
            } else {
                video = new YouTubeVideo();
                video.setName("This video is private.");
                video.setThumbnailURL(Uri.parse("android.resource://com.wordpress.dnvsoft.youtubelite/" + R.mipmap.ic_private_video).toString());
                youTubeVideos.add(video);
            }
        }

        result.setYouTubeVideos(youTubeVideos);

        return result;
    }
}
