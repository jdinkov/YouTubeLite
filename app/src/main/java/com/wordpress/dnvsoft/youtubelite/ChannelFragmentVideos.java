package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelFragmentVideos extends YouTubeItemsFragment {

    String uploadsId;
    private String channelId;

    public static YouTubeItemsFragment newInstance(String channelId) {
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
                        if (!result.isCanceled()) {
                            uploadsId = result.getChannelUploadsId();
                            getVideosFromYouTube();
                        }
                    }
                });
    }

    @Override
    void getVideosFromYouTube() {
        AsyncGetPlaylistItems getPlaylistItems = new AsyncGetPlaylistItems(
                getActivity(), uploadsId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                            if (result.getYouTubeVideos().size() % 20 == 0) {
                                footer.setVisibility(View.VISIBLE);
                            }

                            nextPageToken = result.getNextPageToken();
                            youTubeVideos.addAll(result.getYouTubeVideos());
                            adapter.notifyDataSetChanged();
                            buttonLoadMore.setText(R.string.load_more);
                        }
                    }
                });

        getPlaylistItems.execute();
    }

    @Override
    public void onVideoClick(int position) {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("VIDEO_ID", youTubeVideos.get(position).getId());
        intent.putExtra("VIDEO_TITLE", youTubeVideos.get(position).getName());
        startActivity(intent);
    }
}
