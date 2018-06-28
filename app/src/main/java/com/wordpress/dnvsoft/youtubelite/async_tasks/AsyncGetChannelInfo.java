package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetChannelInfo extends AsyncYoutube {

    private String channelId;

    public AsyncGetChannelInfo(Context c, String channelId, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        YouTube.Channels.List channelList = youtube.channels().list("snippet,contentDetails");
        channelList.setFields("items(id,snippet(title,description,thumbnails/medium/url),contentDetails/relatedPlaylists/uploads)");
        channelList.setId(channelId);
        if (channelId == null) {
            channelList.setMine(true);
        }
        if (accountEmail == null) {
            channelList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        ChannelListResponse response = channelList.execute();
        int size = response.getItems().size();
        ArrayList<YouTubeChannel> youTubeChannels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            YouTubeChannel channel = new YouTubeChannel();
            channel.setId(response.getItems().get(i).getId());
            channel.setName(response.getItems().get(i).getSnippet().getTitle());
            channel.setThumbnailURL(response.getItems().get(i).getSnippet().getThumbnails().getMedium().getUrl());
            channel.setUploadsId(response.getItems().get(i).getContentDetails().getRelatedPlaylists().getUploads());
            channel.setDescription(response.getItems().get(i).getSnippet().getDescription());
            youTubeChannels.add(channel);
        }

        result.setYouTubeChannels(youTubeChannels);
        return result;
    }
}
