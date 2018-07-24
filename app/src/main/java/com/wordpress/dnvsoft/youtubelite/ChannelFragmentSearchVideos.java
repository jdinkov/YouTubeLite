package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItemCount;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.util.ArrayList;

public class ChannelFragmentSearchVideos extends YouTubeItemsFragment {

    private String channelId;
    private String searchParameter;

    public ChannelFragmentSearchVideos() {
    }

    @Override
    void onCreateYouTubeItemsFragment() {
        channelId = getArguments().getString("CHANNEL_ID");
        searchParameter = getArguments().getString("SEARCH_PARAMETER");
        getItemsFromYouTube();
    }

    public static ChannelFragmentSearchVideos newInstance(String channelId, String searchParameter) {
        ChannelFragmentSearchVideos searchVideos = new ChannelFragmentSearchVideos();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        bundle.putString("SEARCH_PARAMETER", searchParameter);
        searchVideos.setArguments(bundle);
        return searchVideos;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View header = inflater.inflate(R.layout.header_fragment_search_videos, listView, false);

        Button button = header.findViewById(R.id.buttonExitSearchVideos);
        TextView textView = header.findViewById(R.id.textViewSearchVideos);
        String textViewString = "\"" + searchParameter + "\"";
        textView.setText(textViewString);
        button.setOnClickListener(onExitClickListener);

        listView.addHeaderView(header, null, false);

        return view;
    }

    View.OnClickListener onExitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    @Override
    void getItemsFromYouTube() {
        AsyncGetItems getItems = new AsyncGetItems(getActivity(),
                channelId, searchParameter, "relevance", nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeItems() != null) {
                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubeItems());
                            getVideoDuration();
                            getPlaylistItemCount();
                            adapter.notifyDataSetChanged();
                        }

                        responseHasReceived = true;

                        updateViewContentInfo();
                        updateViewFooter(YouTubeRequest.RECEIVED);
                    }
                });

        getItems.execute();
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
                                            ((YouTubePlayList) youTubeItems.get(i)).setChannelTitle(playList.getChannelTitle());
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
    void onVideoClick(int position) {
        position--;
        if (youTubeItems.get(position) instanceof YouTubeVideo) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            intent.putExtra("VIDEO_ID", youTubeItems.get(position).getId());
            intent.putExtra("VIDEO_TITLE", youTubeItems.get(position).getName());
            startActivity(intent);
        } else if (youTubeItems.get(position) instanceof YouTubeChannel) {
            Intent intent = new Intent(getActivity(), ChannelActivity.class);
            intent.putExtra("CHANNEL_ID", youTubeItems.get(position).getId());
            intent.putExtra("CHANNEL_NAME", youTubeItems.get(position).getName());
            startActivity(intent);
        } else if (youTubeItems.get(position) instanceof YouTubePlayList) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.root_fragment_videos,
                    ChannelFragmentPlayListItems.newInstance(youTubeItems.get(position).getId()));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    String getContentString() {
        return "No results found";
    }
}
