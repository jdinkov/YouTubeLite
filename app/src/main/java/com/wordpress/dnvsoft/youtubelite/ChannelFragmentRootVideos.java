package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChannelFragmentRootVideos extends Fragment {

    public ChannelFragmentRootVideos() {
    }

    public static ChannelFragmentRootVideos newInstance(String channelId) {
        ChannelFragmentRootVideos rootVideos = new ChannelFragmentRootVideos();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        rootVideos.setArguments(bundle);
        return rootVideos;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_fragment_root_videos, container, false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root_fragment_videos,
                ChannelFragmentVideos.newInstance(getArguments().getString("CHANNEL_ID")));
        fragmentTransaction.commit();

        return view;
    }
}
