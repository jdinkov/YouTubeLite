package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
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

        YouTube.Channels.List channelList = youtube.channels().list("id,contentDetails");
        channelList.setId(channelId);
        if (channelId == null) {
            channelList.setMine(true);
        }
        channelList.setFields("items(id,contentDetails/relatedPlaylists/uploads)");

        ChannelListResponse response = channelList.execute();
        YouTubeChannel channel = new YouTubeChannel();
        channel.setId(response.getItems().get(0).getId());
        channel.setUploadsId(response.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads());

        result.setYouTubeChannel(channel);

        return result;
    }
}
