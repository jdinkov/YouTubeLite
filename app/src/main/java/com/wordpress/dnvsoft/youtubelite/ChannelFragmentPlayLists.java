package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChannelFragmentPlayLists extends Fragment {

    public ChannelFragmentPlayLists() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel_play_lists, container, false);
    }
}
