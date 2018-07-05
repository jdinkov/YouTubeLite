package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelSubscriptions;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class MySubscriptionsFragment extends YouTubeItemsFragment {

    public MySubscriptionsFragment() {
    }

    @Override
    void onCreateYouTubeItemsFragment() {
        getItemsFromYouTube();
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetChannelSubscriptions channelSubscriptions = new AsyncGetChannelSubscriptions(
                getActivity(), null, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannels() != null) {
                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubeChannels());
                            adapter.notifyDataSetChanged();
                        }

                        responseHasReceived = true;

                        updateViewContentInfo();
                        updateViewFooter(YouTubeRequest.RECEIVED);
                    }
                }
        );

        channelSubscriptions.execute();
    }

    @Override
    void onVideoClick(int position) {
        Intent intent = new Intent(getActivity(), ChannelActivity.class);
        intent.putExtra("CHANNEL_ID", youTubeItems.get(position).getId());
        intent.putExtra("CHANNEL_NAME", youTubeItems.get(position).getName());
        startActivity(intent);
    }

    @Override
    String getContentString() {
        return "You have no subscriptions.";
    }
}
