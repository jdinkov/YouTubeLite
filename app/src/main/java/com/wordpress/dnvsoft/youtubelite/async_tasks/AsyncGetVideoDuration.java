package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncGetVideoDuration extends AsyncYoutube {

    private String videoIds;

    public AsyncGetVideoDuration(Context c, String videoIds, TaskCompleted callback) {
        super(c, callback);
        this.videoIds = videoIds;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        YouTube.Videos.List videoList = youtube.videos().list("snippet,contentDetails");
        videoList.setFields("items(id,snippet/channelTitle,contentDetails/duration)");
        videoList.setId(videoIds);
        if (accountEmail == null) {
            videoList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        VideoListResponse response = videoList.execute();
        int size = response.getItems().size();
        ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            YouTubeVideo video = new YouTubeVideo();
            video.setId(response.getItems().get(i).getId());
            video.setChannelTitle(response.getItems().get(i).getSnippet().getChannelTitle());
            video.setDuration(getDuration(response.getItems().get(i).getContentDetails().getDuration()));
            youTubeVideos.add(video);
        }

        result.setYouTubeVideos(youTubeVideos);

        return result;
    }

    private String getDuration(String input) {
        Pattern pattern = Pattern.compile("([0-9]+[DHMS])");
        Matcher matcher = pattern.matcher(input);

        HashMap<String, String> matches = new HashMap<>();
        while (matcher.find()) {
            String temp = matcher.group();
            int length = temp.length();
            matches.put(temp.substring(length - 1), temp.substring(0, length - 1));
        }

        StringBuilder duration = new StringBuilder();
        int currentDuration = 0;
        if (matches.containsKey("D")) {
            currentDuration = (Integer.valueOf(matches.get("D")) * 24);
        }
        if (matches.containsKey("H")) {
            int temp = Integer.valueOf(matches.get("H"));
            currentDuration += temp;
            duration.append(currentDuration);
            duration.append(":");
        }
        if (matches.containsKey("M")) {
            duration.append(twoDigitValue(matches.get("M")));
            duration.append(":");
        } else {
            duration.append("00:");
        }
        if (matches.containsKey("S")) {
            duration.append(twoDigitValue(matches.get("S")));
        } else {
            duration.append("00");
        }

        return duration.toString();
    }

    private String twoDigitValue(String input) {
        StringBuilder twoDigitString = new StringBuilder();
        if (input.length() == 1) {
            twoDigitString.append("0");
        }
        twoDigitString.append(input);

        return twoDigitString.toString();
    }
}
