package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

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
        YouTubeChannel channel = new YouTubeChannel();
        if (response.getItems().size() > 0) {
            channel.setId(response.getItems().get(0).getId());
            channel.setName(response.getItems().get(0).getSnippet().getTitle());
            channel.setThumbnailURL(response.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl());
            channel.setUploadsId(response.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads());
            channel.setDescription(response.getItems().get(0).getSnippet().getDescription());
            result.setYouTubeChannel(channel);
        }

        return result;
    }
}
