package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncGetSubscriptionInfo extends AsyncYoutube {

    private String channelId;

    public AsyncGetSubscriptionInfo(Context c, String channelId, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        YouTube.Subscriptions.List subscriptionsList = youtube.subscriptions().list("id");
        subscriptionsList.setFields("items/id");
        subscriptionsList.setForChannelId(channelId);
        subscriptionsList.setMine(true);

        SubscriptionListResponse response = subscriptionsList.execute();
        if (response.getItems().size() > 0) {
            YouTubeChannel channel = new YouTubeChannel();
            channel.setSubscriptionId(response.getItems().get(0).getId());
            result.setYouTubeChannel(channel);
        }

        return result;
    }

    @Override
    protected void onPostExecute(YouTubeResult result) {
        super.onPostExecute(result);
    }
}
