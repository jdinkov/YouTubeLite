package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetVideos;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.util.ArrayList;

public class VideoFragmentVideos extends YouTubeItemsFragment {

    private String videoID;
    private String playlistId;

    public static VideoFragmentVideos newInstance(
            ArrayList<YouTubeVideo> items, String playlistId, String videoID, String nextPageToken) {
        VideoFragmentVideos videoFragmentVideos = new VideoFragmentVideos();
        Bundle bundle = new Bundle();
        bundle.putString("ITEMS", YouTubeItemJsonHelper.toJson(items));
        bundle.putString("PLAYLIST_ID", playlistId);
        bundle.putString("NEXT_PAGE_TOKEN", nextPageToken);
        bundle.putString("VIDEO_ID", videoID);
        videoFragmentVideos.setArguments(bundle);
        return videoFragmentVideos;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        youTubeItems.addAll(YouTubeItemJsonHelper.fromJson(YouTubeVideo.class, getArguments().getString("ITEMS")));
        playlistId = getArguments().getString("PLAYLIST_ID");
        videoID = getArguments().getString("VIDEO_ID");
        nextPageToken = getArguments().getString("NEXT_PAGE_TOKEN");

        if (youTubeItems.isEmpty()) {
            getItemsFromYouTube();
        }
    }

    @Override
    void getItemsFromYouTube() {
        if (playlistId == null) {
            getRelatedVideos();
        } else {
            getPlaylistItems();
        }
    }

    private void getRelatedVideos() {
        AsyncGetVideos getVideos = new AsyncGetVideos(getContext(), videoID, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        asyncTaskCompleted.onTaskComplete(result);
                    }
                });

        getVideos.execute();
    }

    private void getPlaylistItems() {
        AsyncGetPlaylistItems playlistItems = new AsyncGetPlaylistItems(
                getActivity(), playlistId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        asyncTaskCompleted.onTaskComplete(result);
                    }
                }
        );

        playlistItems.execute();
    }

    @Override
    public void onVideoClick(int position) {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        if (playlistId != null) {
            int videoPosition = youTubeItems.size() - position - 1;

            intent.putExtra("PLAYLIST_ID", playlistId);
            intent.putExtra("VIDEO_POSITION", videoPosition);
            intent.putExtra("NEXT_PAGE_TOKEN", nextPageToken);
            intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeItems));
        } else {
            intent.putExtra("VIDEO_ID", youTubeItems.get(position).getId());
            intent.putExtra("VIDEO_TITLE", youTubeItems.get(position).getName());
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    String getContentString() {
        return "This channel has no videos.";
    }
}
