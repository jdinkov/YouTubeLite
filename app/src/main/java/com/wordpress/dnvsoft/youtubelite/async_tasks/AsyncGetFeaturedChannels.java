package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncGetFeaturedChannels extends AsyncYoutube {

    private String channelId;

    public AsyncGetFeaturedChannels(Context c, String channelId, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTube.Channels.List channelList = youtube.channels().list("brandingSettings");
        channelList.setFields("items/brandingSettings/channel/featuredChannelsUrls");
        channelList.setId(channelId);
        if (accountEmail == null) {
            channelList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        ChannelListResponse response = channelList.execute();
        if (response.getItems().size() > 0) {
            YouTubeChannel youTubeChannel = new YouTubeChannel();
            youTubeChannel.setFeaturedChannelsUrls(response.getItems().get(0).getBrandingSettings().getChannel().getFeaturedChannelsUrls());
            result.setYouTubeChannel(youTubeChannel);
        }

        return result;
    }
}
