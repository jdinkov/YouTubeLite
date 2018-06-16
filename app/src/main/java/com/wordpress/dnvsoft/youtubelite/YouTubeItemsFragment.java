package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wordpress.dnvsoft.youtubelite.adapters.YouTubeItemAdapter;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;
import com.wordpress.dnvsoft.youtubelite.network.Network;

import java.util.ArrayList;

public abstract class YouTubeItemsFragment extends Fragment {

    LinearLayout footer;
    Button buttonLoadMore;
    YouTubeItemAdapter<YouTubeVideo> adapter;
    String nextPageToken;
    ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();
    String playlistId;

    public YouTubeItemsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateYouTubeItemsFragment();
    }

    public abstract void onCreateYouTubeItemsFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_videos, container, false);

        ListView listView = view.findViewById(R.id.listViewVideo);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = footer.findViewById(R.id.buttonFooterMain);
        if (Network.IsDeviceOnline(getActivity())) {
            footer.setVisibility(View.GONE);
        } else {
            footer.setVisibility(View.VISIBLE);
            buttonLoadMore.setText(R.string.refresh);
        }
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        listView.setOnItemClickListener(onItemClickListener);

        adapter = new YouTubeItemAdapter<>(getContext(), youTubeVideos);
        listView.setAdapter(adapter);

        if (playlistId != null) {
            footer.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();

        return view;
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.GONE);
            getVideosFromYouTube();
        }
    };

    abstract void getVideosFromYouTube();

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onVideoClick(position);
        }
    };

    public abstract void onVideoClick(int position);
}
