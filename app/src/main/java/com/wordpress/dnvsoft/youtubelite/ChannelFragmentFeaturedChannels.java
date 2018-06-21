package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelSubscriptions;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetFeaturedChannels;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.util.ArrayList;

public class ChannelFragmentFeaturedChannels extends YouTubeItemsFragment {

    private String channelId;
    private ArrayList<String> featuredChannelIds = new ArrayList<>();
    private ArrayList<YouTubeChannel> featuredChannels = new ArrayList<>();
    private ArrayList<YouTubeChannel> channelSubscriptions = new ArrayList<>();
    private int spinnerPosition = 0;
    private String[] spinnerEntities = new String[]{
            "Featured Channels",
            "Subscriptions"
    };

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
        channelId = getArguments().getString("CHANNEL_ID");
        getItemsFromYouTube();
    }

    @Override
    void onStateRestored() {
        updateViewContentInfo("This channel doesn't feature any other channels.");
        updateViewFooter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = view.findViewById(R.id.listViewItem);
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.channel_featured_channels_header, listView, false);
        Spinner spinner = header.findViewById(R.id.spinnerFeaturedChannels);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.support_simple_spinner_dropdown_item, spinnerEntities);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(onItemClickListener);

        listView.addHeaderView(header);

        return view;
    }

    AdapterView.OnItemSelectedListener onItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spinnerPosition != position) {
                spinnerPosition = position;
                youTubeItems.clear();
                switch (spinnerPosition) {
                    case 0: {
                        youTubeItems.addAll(featuredChannels);
                    }
                    break;
                    case 1: {
                        youTubeItems.addAll(channelSubscriptions);
                    }
                    break;
                }
                updateViewFooter();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    void getItemsFromYouTube() {
        getFeaturedChannels();
        getChannelSubscriptions();
    }

    private void getFeaturedChannels() {
        AsyncGetFeaturedChannels featuredChannels = new AsyncGetFeaturedChannels(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel() != null) {
                            featuredChannelIds.addAll(result.getYouTubeChannel().getFeaturedChannelsUrls());
                            for (String item : featuredChannelIds) {
                                getChannelInfo(item).execute();
                            }
                        }

                        if (featuredChannelIds.size() == 0) {
                            TextView textView = getView().findViewById(R.id.textViewContentInfo);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.doesnt_feature_any_other_channels);
                        }
                    }
                }
        );

        featuredChannels.execute();
    }

    private AsyncGetChannelInfo getChannelInfo(String id) {
        return new AsyncGetChannelInfo(getActivity(), id,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel() != null) {
                            featuredChannels.add(result.getYouTubeChannel());
                            if (spinnerPosition == 0) {
                                youTubeItems.add(result.getYouTubeChannel());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getChannelSubscriptions() {
        AsyncGetChannelSubscriptions subscriptions = new AsyncGetChannelSubscriptions(
                getActivity(), channelId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannels() != null) {
                            nextPageToken = result.getNextPageToken();
                            channelSubscriptions.addAll(result.getYouTubeChannels());
                        }
                    }
                }
        );

        subscriptions.execute();
    }

    @Override
    public void onVideoClick(int position) {
        position--;
        Intent intent = new Intent(getActivity(), ChannelActivity.class);
        intent.putExtra("CHANNEL_ID", youTubeItems.get(position).getId());
        intent.putExtra("CHANNEL_NAME", youTubeItems.get(position).getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
