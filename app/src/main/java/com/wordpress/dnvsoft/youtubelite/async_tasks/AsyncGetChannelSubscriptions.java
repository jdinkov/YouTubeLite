package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetChannelSubscriptions extends AsyncYoutube {

    private String channelId;
    private String pageToken;

    public AsyncGetChannelSubscriptions(Context c, String channelId, String pageToken, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
        this.pageToken = pageToken;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        ArrayList<YouTubeChannel> youTubeChannels = new ArrayList<>();

        YouTube.Subscriptions.List subscriptionList = youtube.subscriptions().list("snippet ");
        subscriptionList.setFields("items(snippet(title,resourceId/channelId,thumbnails/medium/url))");
        subscriptionList.setChannelId(channelId);
        if (channelId == null) {
            subscriptionList.setMine(true);
        }
        subscriptionList.setMaxResults(20L);
        subscriptionList.setPageToken(pageToken);
        if (accountEmail == null) {
            subscriptionList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        SubscriptionListResponse response = subscriptionList.execute();
        result.setNextPageToken(response.getNextPageToken());
        int size = response.getItems().size();
        for (int i = 0; i < size; i++) {
            YouTubeChannel youTubeChannel = new YouTubeChannel();
            youTubeChannel.setId(response.getItems().get(i).getSnippet().getResourceId().getChannelId());
            youTubeChannel.setName(response.getItems().get(i).getSnippet().getTitle());
            youTubeChannel.setThumbnailURL(response.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            youTubeChannels.add(youTubeChannel);
        }

        result.setYouTubeChannels(youTubeChannels);

        return result;
    }
}
