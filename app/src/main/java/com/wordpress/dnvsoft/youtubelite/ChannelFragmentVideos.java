package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelFragmentVideos extends YouTubeItemsFragment {

    String uploadsId;
    private String channelId;

    public static ChannelFragmentVideos newInstance(String channelId) {
        ChannelFragmentVideos channelFragmentVideos = new ChannelFragmentVideos();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        channelFragmentVideos.setArguments(bundle);
        return channelFragmentVideos;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        channelId = getArguments().getString("CHANNEL_ID");
        getChannelUploadsId().execute();
    }

    private AsyncGetChannelInfo getChannelUploadsId() {
        return new AsyncGetChannelInfo(getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannels() != null) {
                            uploadsId = result.getYouTubeChannels().get(0).getUploadsId();
                            getItemsFromYouTube();
                        }
                    }
                });
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetPlaylistItems getPlaylistItems = new AsyncGetPlaylistItems(
                getActivity(), uploadsId, nextPageToken,
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
        intent.putExtra("VIDEO_ID", youTubeItems.get(position).getId());
        intent.putExtra("VIDEO_TITLE", youTubeItems.get(position).getName());
        startActivity(intent);
    }

    @Override
    String getContentString() {
        return "This channel has no videos.";
    }
}
