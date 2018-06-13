package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;

public class AsyncEditCommentReply extends AsyncYoutube {

    private String commentId;
    private String commentText;

    public AsyncEditCommentReply(Context c, String commentId, String commentText, TaskCompleted callback) {
        super(c, callback);
        this.commentId = commentId;
        this.commentText = commentText;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        Comment comment = new Comment();
        CommentSnippet commentSnippet = new CommentSnippet();

        commentSnippet.setTextOriginal(commentText);
        comment.setId(commentId);
        comment.setSnippet(commentSnippet);

        YouTube.Comments.Update update = youtube.comments().update("snippet", comment);
        update.execute();

        return result;
    }
}
