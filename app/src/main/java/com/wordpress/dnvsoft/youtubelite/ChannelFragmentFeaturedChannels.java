package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;

public class ChannelFragmentFeaturedChannels extends YouTubeItemsFragment {

    public ChannelFragmentFeaturedChannels() {
    }

    public static ChannelFragmentFeaturedChannels newInstance(String channelId) {
        ChannelFragmentFeaturedChannels fragmentFeaturedChannels = new ChannelFragmentFeaturedChannels();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        fragmentFeaturedChannels.setArguments(bundle);
        return fragmentFeaturedChannels;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {

    }

    @Override
    void getItemsFromYouTube() {

    }

    @Override
    public void onVideoClick(int position) {

    }
}
