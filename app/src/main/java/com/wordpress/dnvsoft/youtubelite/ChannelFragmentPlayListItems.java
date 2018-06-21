package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelFragmentPlayListItems extends YouTubeItemsFragment {

    private String playlistId;
    private String nextPageToken;

    public ChannelFragmentPlayListItems() {
    }

    public static ChannelFragmentPlayListItems newInstance(String playlistId) {
        ChannelFragmentPlayListItems channelFragmentPlayListItems = new ChannelFragmentPlayListItems();
        Bundle bundle = new Bundle();
        bundle.putString("PLAYLIST_ID", playlistId);
        channelFragmentPlayListItems.setArguments(bundle);
        return channelFragmentPlayListItems;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        playlistId = getArguments().getString("PLAYLIST_ID");
        getItemsFromYouTube();
    }

    @Override
    void onStateRestored() {
        updateViewContentInfo("This playlist is empty.");
        updateViewFooter();
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetPlaylistItems getPlaylistItems = new AsyncGetPlaylistItems(
                getActivity(), playlistId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        asyncTaskCompleted.onTaskComplete(result);
                    }
                });

        getPlaylistItems.execute();
    }

    @Override
    public void onVideoClick(int position) {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("PLAYLIST_ID", playlistId);
        intent.putExtra("VIDEO_POSITION", position);
        intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeItems));
        startActivity(intent);
    }
}
