package com.wordpress.dnvsoft.youtubelite;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncDeleteSubscription;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetSubscriptionInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncInsertSubscription;
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
            if (!isSubscribed) {
                insertSubscription();
            } else {
                deleteSubscriptionDialog();
            }
        }
    };

    private void getChannelInfo() {
        AsyncGetChannelInfo channelInfo = new AsyncGetChannelInfo(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannels() != null) {
                            aboutString = result.getYouTubeChannels().get(0).getDescription();
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

    private void insertSubscription() {
        AsyncInsertSubscription insertSubscription = new AsyncInsertSubscription(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel() != null) {
                            isSubscribed = true;
                            subscriptionId = result.getYouTubeChannel().getSubscriptionId();
                            Toast.makeText(getActivity(), "Subscription added", Toast.LENGTH_SHORT).show();
                            updateButtonSubscribe();
                        }
                    }
                }
        );

        insertSubscription.execute();
    }

    private void deleteSubscription() {
        AsyncDeleteSubscription deleteSubscription = new AsyncDeleteSubscription(
                getActivity(), subscriptionId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            isSubscribed = false;
                            subscriptionId = null;
                            Toast.makeText(getActivity(), "Subscription removed", Toast.LENGTH_SHORT).show();
                            updateButtonSubscribe();
                        }
                    }
                }
        );

        deleteSubscription.execute();
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

    private void deleteSubscriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to unsubscribe from " + getActivity().getTitle() + "?");
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSubscription();
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }
}
