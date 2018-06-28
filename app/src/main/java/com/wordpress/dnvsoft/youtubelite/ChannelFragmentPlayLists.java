package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlayLists;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItemCount;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.util.ArrayList;

public class ChannelFragmentPlayLists extends YouTubeItemsFragment {

    private String channelId;

    public ChannelFragmentPlayLists() {
    }

    public static ChannelFragmentPlayLists newInstance(String channelId) {
        ChannelFragmentPlayLists channelFragmentPlayLists = new ChannelFragmentPlayLists();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        channelFragmentPlayLists.setArguments(bundle);
        return channelFragmentPlayLists;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        channelId = getArguments().getString("CHANNEL_ID");
        getItemsFromYouTube();
    }

    @Override
    void onStateRestored() {
        updateViewContentInfo();
        updateViewFooter();
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetPlayLists playLists = new AsyncGetPlayLists(
                getActivity(), channelId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubePlayLists() != null) {
                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubePlayLists());
                            getPlaylistItemCount();
                            adapter.notifyDataSetChanged();
                        }

                        responseHasReceived = true;

                        updateViewContentInfo();
                        updateViewFooter();
                    }
                });

        playLists.execute();
    }

    private ArrayList<String> getPlaylistIds() {
        ArrayList<String> playlistIds = new ArrayList<>();
        for (YouTubeItem item : youTubeItems) {
            if (item instanceof YouTubePlayList) {
                if (item.getItemCount() == null) {
                    playlistIds.add(item.getId());
                }
            }
        }

        return playlistIds;
    }

    private void getPlaylistItemCount() {
        ArrayList<String> playlistIds = getPlaylistIds();
        if (!playlistIds.isEmpty()) {
            AsyncGetPlaylistItemCount itemCount = new AsyncGetPlaylistItemCount(
                    getActivity(), playlistIds,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getYouTubePlayLists() != null) {
                                for (int i = 0; i < youTubeItems.size(); i++) {
                                    for (YouTubePlayList playList : result.getYouTubePlayLists()) {
                                        if (youTubeItems.get(i).getId().equals(playList.getId())) {
                                            youTubeItems.get(i).setItemCount(playList.getItemCount());
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
            );

            itemCount.execute();
        }
    }

    @Override
    public void onVideoClick(int position) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root_fragment_play_lists,
                ChannelFragmentPlayListItems.newInstance(youTubeItems.get(position).getId()));
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    String getContentString() {
        return "This channel has no playlists.";
    }
}
