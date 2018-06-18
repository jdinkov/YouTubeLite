package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChannelFragmentRootPlayLists extends Fragment {


    public ChannelFragmentRootPlayLists() {
    }

    public static ChannelFragmentRootPlayLists newInstance(String channelId) {
        ChannelFragmentRootPlayLists channelFragmentRootPlayLists = new ChannelFragmentRootPlayLists();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        channelFragmentRootPlayLists.setArguments(bundle);
        return channelFragmentRootPlayLists;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_fragmet_root_play_lists, container, false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root_fragment_play_lists,
                ChannelFragmentPlayLists.newInstance(getArguments().getString("CHANNEL_ID")));
        fragmentTransaction.commit();

        return view;
    }
}
