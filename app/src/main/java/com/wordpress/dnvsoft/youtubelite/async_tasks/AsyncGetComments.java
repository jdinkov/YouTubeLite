package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeComment;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetComments extends AsyncYoutube {

    private String id;
    private String nextPageToken;
    private String parentId;

    public AsyncGetComments(Context c, String id, String pageToken, String parentId, TaskCompleted callback) {
        super(c, callback);
        this.id = id;
        this.nextPageToken = pageToken;
        this.parentId = parentId;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {

        ArrayList<YouTubeComment> youTubeComments = new ArrayList<>();

        YouTube.Comments.List commentsList = youtube.comments().list("snippet");
        commentsList.setId(id);
        commentsList.setPageToken(nextPageToken);
        commentsList.setParentId(parentId);
        commentsList.setMaxResults((long) 20);
        commentsList.setFields("items(id,snippet(authorDisplayName,authorProfileImageUrl,authorChannelId,likeCount,textOriginal,viewerRating)),nextPageToken");
        if (accountEmail == null) {
            commentsList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        CommentListResponse response = commentsList.execute();
        result.setNextPageToken(response.getNextPageToken());
        int responseSize = response.getItems().size();
        for (int i = 0; i < responseSize; i++) {
            YouTubeComment comment = new YouTubeComment();
            comment.setID(response.getItems().get(i).getId());
            comment.setAuthorDisplayName(response.getItems().get(i).getSnippet().getAuthorDisplayName());
            comment.setAuthorImageUrl(response.getItems().get(i).getSnippet().getAuthorProfileImageUrl());
            comment.setCommentText(response.getItems().get(i).getSnippet().getTextOriginal());
            comment.setViewerRating(response.getItems().get(i).getSnippet().getViewerRating());
            comment.setLikeCount(response.getItems().get(i).getSnippet().getLikeCount().toString());
            String authorChannelId = response.getItems().get(i).getSnippet().getAuthorChannelId().toString();
            comment.setAuthorChannelId(authorChannelId.substring(7, authorChannelId.length() - 1));

            youTubeComments.add(comment);
        }

        result.setCommentReplies(youTubeComments);
        return result;
    }
}
