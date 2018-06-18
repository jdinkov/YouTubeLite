package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    void getItemsFromYouTube() {
        AsyncGetPlaylistItems getPlaylistItems = new AsyncGetPlaylistItems(
                getActivity(), playlistId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                            if (result.getYouTubeVideos().size() % 20 == 0) {
                                footer.setVisibility(View.VISIBLE);
                            }

                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubeVideos());
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
        intent.putExtra("PLAYLIST_ID", playlistId);
        intent.putExtra("VIDEO_POSITION", position);
        intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeItems));
        startActivity(intent);
    }
}
