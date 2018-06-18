package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlayLists;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelFragmentPlayLists extends YouTubeItemsFragment {

    private String channelId;
    private String nextPageToken;

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
    void getItemsFromYouTube() {
        AsyncGetPlayLists playLists = new AsyncGetPlayLists(
                getActivity(), channelId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubePlayLists() != null) {
                            if (result.getYouTubePlayLists().size() % 20 == 0) {
                                footer.setVisibility(View.VISIBLE);
                            }

                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubePlayLists());
                            adapter.notifyDataSetChanged();
                            buttonLoadMore.setText(R.string.load_more);
                        }
                    }
                });

        playLists.execute();
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
}
