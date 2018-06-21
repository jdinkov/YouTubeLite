package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetSubscriptionInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelFragmentAbout extends Fragment {

    private String channelId;
    private String subscriptionId;
    private String aboutString;
    private boolean isSubscribed;

    public ChannelFragmentAbout() {
    }

    public static ChannelFragmentAbout newInstance(String channelId) {
        ChannelFragmentAbout fragmentAbout = new ChannelFragmentAbout();
        Bundle bundle = new Bundle();
        bundle.putString("CHANNEL_ID", channelId);
        fragmentAbout.setArguments(bundle);
        return fragmentAbout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelId = getArguments().getString("CHANNEL_ID");
        getChannelInfo();
        getSubscriptionInfo();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateTextViewAbout();
        updateButtonSubscribe();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_fragment_about, container, false);

        Button buttonSubscribe = view.findViewById(R.id.buttonSubscribe);
        buttonSubscribe.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private void getChannelInfo() {
        AsyncGetChannelInfo channelInfo = new AsyncGetChannelInfo(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel() != null) {
                            aboutString = result.getYouTubeChannel().getDescription();
                            updateTextViewAbout();
                        }
                    }
                }
        );

        channelInfo.execute();
    }

    private void getSubscriptionInfo() {
        AsyncGetSubscriptionInfo subscriptionsInfo = new AsyncGetSubscriptionInfo(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel() != null) {
                            isSubscribed = true;
                            subscriptionId = result.getYouTubeChannel().getSubscriptionId();
                            updateButtonSubscribe();
                        }
                    }
                }
        );

        subscriptionsInfo.execute();
    }

    private void updateTextViewAbout() {
        TextView textViewAbout = getView().findViewById(R.id.textViewAbout);
        if (aboutString != null) {
            textViewAbout.setText(aboutString);
        } else {
            textViewAbout.setText(R.string.text_view_channel_info);
        }
    }

    private void updateButtonSubscribe() {
        Button buttonSubscribe = getView().findViewById(R.id.buttonSubscribe);
        if (isSubscribed) {
            buttonSubscribe.setText(R.string.subscribed);
            buttonSubscribe.setBackgroundColor(getResources().getColor(R.color.buttonSubscribeGrey));
        } else {
            buttonSubscribe.setText(R.string.subscribe);
            buttonSubscribe.setBackgroundColor(getResources().getColor(R.color.buttonSubscribeRed));
        }
    }
}
