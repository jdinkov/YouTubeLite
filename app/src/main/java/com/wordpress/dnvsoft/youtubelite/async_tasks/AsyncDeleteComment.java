package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncDeleteComment extends AsyncYoutube {

    private String commentId;

    public AsyncDeleteComment(Context c, String commentId, TaskCompleted callback) {
        super(c, callback);
        this.commentId = commentId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        youtube.comments().delete(commentId).execute();

        return result;
    }
}
