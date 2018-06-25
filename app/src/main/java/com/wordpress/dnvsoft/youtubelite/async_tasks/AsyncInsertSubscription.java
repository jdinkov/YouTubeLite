package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncInsertSubscription extends AsyncYoutube {

    private String channelId;

    public AsyncInsertSubscription(Context c, String channelId, TaskCompleted callback) {
        super(c, callback);
        this.channelId = channelId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        Subscription subscription = new Subscription();
        SubscriptionSnippet snippet = new SubscriptionSnippet();
        ResourceId resourceId = new ResourceId();
        resourceId.set("channelId", channelId);
        resourceId.set("kind", "youtube#channel");

        snippet.setResourceId(resourceId);
        subscription.setSnippet(snippet);

        YouTube.Subscriptions.Insert subscriptionInsert
                = youtube.subscriptions().insert("snippet", subscription);
        subscriptionInsert.setFields("id");
        Subscription response = subscriptionInsert.execute();

        int size = response.size();
        if (size > 0) {
            YouTubeChannel channel = new YouTubeChannel();
            channel.setSubscriptionId(response.getId());
            result.setYouTubeChannel(channel);
        }

        return result;
    }
}
