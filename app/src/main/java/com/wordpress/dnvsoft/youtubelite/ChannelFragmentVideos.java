package com.wordpress.dnvsoft.youtubelite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

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
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getActivity().
                getSharedPreferences("CHANNEL_FRAGMENT_VIDEOS", Context.MODE_PRIVATE);
        nextPageToken = preferences.getString("VIDEOS_PAGE_TOKEN", null);
        uploadsId = preferences.getString("CHANNEL_UPLOADS_ID", null);
        String tempString = preferences.getString("VIDEOS", null);
        if (tempString != null && youTubeItems.isEmpty()) {
            youTubeItems.addAll(YouTubeItemJsonHelper.fromJson(YouTubeVideo.class, tempString));
            updateViewContentInfo();
            updateViewFooter(YouTubeRequest.RECEIVED);
        } else if (youTubeItems.isEmpty()) {
            getChannelUploadsId().execute();
        }
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
        intent.putExtra("VIDEO_DURATION", ((YouTubeVideo) youTubeItems.get(position)).getDuration());
        startActivity(intent);
    }

    @Override
    String getContentString() {
        return "This channel has no videos.";
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getActivity().
                getSharedPreferences("CHANNEL_FRAGMENT_VIDEOS", Context.MODE_PRIVATE).edit();
        editor.putString("VIDEOS_PAGE_TOKEN", nextPageToken);
        editor.putString("VIDEOS", YouTubeItemJsonHelper.toJson(youTubeItems));
        editor.putString("CHANNEL_UPLOADS_ID", uploadsId);
        editor.apply();
    }
}
