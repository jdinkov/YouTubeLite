package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlaylist;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetItems extends AsyncYoutube {

    private String parameter;
    private String orderBy;
    private String pageToken;

    public AsyncGetItems(Context context, String parameter, String orderBy,
                         String pageToken, TaskCompleted callback) {
        super(context, callback);
        this.parameter = parameter;
        this.orderBy = orderBy;
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubeItem item = null;
        ArrayList<YouTubeItem> youTubeItems = new ArrayList<>();

        YouTube.Search.List searchList = youtube.search().list("snippet");
        String fields = "items(id,snippet(title,thumbnails/medium/url)),nextPageToken";
        searchList.setFields(fields);
        searchList.setPageToken(pageToken);
        searchList.setOrder(orderBy);
        searchList.setQ(parameter);
        searchList.setMaxResults((long) 20);
        if (accountEmail == null) {
            searchList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        SearchListResponse searchListResponse = searchList.execute();
        pageToken = searchListResponse.getNextPageToken();
        int size = searchListResponse.getItems().size();
        for (int i = 0; i < size; i++) {
            switch (searchListResponse.getItems().get(i).getId().getKind()) {
                case "youtube#video": {
                    item = new YouTubeVideo();
                    item.setId(searchListResponse.getItems().get(i).getId().getVideoId());
                }
                break;
                case "youtube#playlist": {
                    item = new YouTubePlaylist();
                    item.setId(searchListResponse.getItems().get(i).getId().getPlaylistId());
                }
                break;
                case "youtube#channel": {
                    item = new YouTubeChannel();
                    item.setId(searchListResponse.getItems().get(i).getId().getChannelId());
                }
                break;
            }

            item.setName(searchListResponse.getItems().get(i).getSnippet().getTitle());
            item.setThumbnailURL(searchListResponse.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            youTubeItems.add(item);
        }

        result.setNextPageToken(pageToken);
        result.setYouTubeItems(youTubeItems);
        return result;
    }
}
