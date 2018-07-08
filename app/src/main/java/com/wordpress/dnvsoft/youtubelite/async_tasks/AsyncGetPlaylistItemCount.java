package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;
import android.text.TextUtils;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncGetPlaylistItemCount extends AsyncYoutube {

    private ArrayList<String> playlistIds;

    public AsyncGetPlaylistItemCount(Context c, ArrayList<String> playlistIds, TaskCompleted callback) {
        super(c, callback);
        this.playlistIds = playlistIds;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        ArrayList<YouTubePlayList> youTubePlayLists = new ArrayList<>();

        YouTube.Playlists.List playlistList = youtube.playlists().list("snippet,contentDetails");
        playlistList.setFields("items(id,snippet/channelTitle,contentDetails/itemCount)");
        playlistList.setId(TextUtils.join(",", playlistIds));
        if (accountEmail == null) {
            playlistList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        PlaylistListResponse response = playlistList.execute();
        int size = response.getItems().size();
        for (int i = 0; i < size; i++) {
            YouTubePlayList playList = new YouTubePlayList();
            playList.setId(response.getItems().get(i).getId());
            playList.setChannelTitle(response.getItems().get(i).getSnippet().getChannelTitle());
            playList.setItemCount(response.getItems().get(i).getContentDetails().getItemCount().toString());
            youTubePlayLists.add(playList);
        }

        result.setYouTubePlayLists(youTubePlayLists);

        return result;
    }
}
