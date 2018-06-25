package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncDeleteSubscription extends AsyncYoutube {

    private String subscriptionId;

    public AsyncDeleteSubscription(Context c, String subscriptionId, TaskCompleted callback) {
        super(c, callback);
        this.subscriptionId = subscriptionId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        youtube.subscriptions().delete(subscriptionId).execute();
        return result;
    }
}
