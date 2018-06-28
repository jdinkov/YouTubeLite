package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.content.Context;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncPlaylistThumbnail extends AsyncYoutube {

    private String PlaylistID;

    public AsyncPlaylistThumbnail(Context context, String playlistID, TaskCompleted callback) {
        super(context, callback);
        this.PlaylistID = playlistID;
    }

    @Override
    YouTubeResult DoItInBackground() throws IOException {
        PlaylistListResponse playlistListResponse;

        YouTube.Playlists.List playList = youtube.playlists().list("snippet");
        playList.setId(PlaylistID);
        playList.setFields("items/snippet/thumbnails/maxres/url");
        if (accountEmail == null) {
            playList.setKey(YoutubeInfo.DEVELOPER_KEY);
        }

        playlistListResponse = playList.execute();
        String maxResUrl = playlistListResponse.getItems().get(0).getSnippet().getThumbnails().getMaxres().getUrl();

        YouTubePlayList item = new YouTubePlayList();
        ArrayList<YouTubePlayList> items = new ArrayList<>();
        item.setThumbnailURL(maxResUrl);
        items.add(item);

        result.setYouTubePlayLists(items);
        return result;
    }
}
