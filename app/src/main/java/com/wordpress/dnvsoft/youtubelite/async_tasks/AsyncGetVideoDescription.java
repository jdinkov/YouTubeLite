package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AsyncGetVideoDescription extends AsyncYoutube {

    private String videoID;

    public AsyncGetVideoDescription(Context context, String id, TaskCompleted callback) {
        super(context, callback);
        this.videoID = id;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTubeVideo youTubeVideo = new YouTubeVideo();
        YouTube.Videos.List videoList = youtube.videos().list("snippet,statistics");
        videoList.setId(videoID);
        videoList.setFields("items(snippet(publishedAt,channelId,title,description),statistics(viewCount,likeCount,dislikeCount,commentCount))");
        if (accountEmail == null) {
            videoList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        VideoListResponse videoListResponse = videoList.execute();
        youTubeVideo.setChannelId(videoListResponse.getItems().get(0).getSnippet().getChannelId());
        youTubeVideo.setName(videoListResponse.getItems().get(0).getSnippet().getTitle());
        youTubeVideo.setDescription(videoListResponse.getItems().get(0).getSnippet().getDescription());
        DateTime date = videoListResponse.getItems().get(0).getSnippet().getPublishedAt();
        Date d = new Date(date.getValue());
        DateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);
        youTubeVideo.setPublishedAt(dateFormat.format(d));
        youTubeVideo.setViewCount(videoListResponse.getItems().get(0).getStatistics().getViewCount().toString());
        BigInteger tempLikeCount = videoListResponse.getItems().get(0).getStatistics().getLikeCount();
        youTubeVideo.setLikeCount(tempLikeCount != null ?
                videoListResponse.getItems().get(0).getStatistics().getLikeCount().toString() : "");
        BigInteger tempDislikeCount = videoListResponse.getItems().get(0).getStatistics().getDislikeCount();
        youTubeVideo.setDislikeCount(tempDislikeCount != null ?
                videoListResponse.getItems().get(0).getStatistics().getDislikeCount().toString() : "");
        if (videoListResponse.getItems().get(0).getStatistics().getCommentCount() != null) {
            youTubeVideo.setCommentCount(videoListResponse.getItems().get(0).getStatistics().getCommentCount().toString());
        }

        ArrayList<YouTubeVideo> youTubeVideoArray = new ArrayList<>(Collections.singleton(youTubeVideo));
        result.setYouTubeVideos(youTubeVideoArray);

        return result;
    }
}
